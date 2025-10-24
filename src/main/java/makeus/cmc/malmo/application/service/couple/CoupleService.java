package makeus.cmc.malmo.application.service.couple;

import lombok.RequiredArgsConstructor;
import makeus.cmc.malmo.adaptor.in.aop.CheckCoupleMember;
import makeus.cmc.malmo.adaptor.in.aop.CheckValidMember;
import makeus.cmc.malmo.application.helper.chat_room.ChatRoomCommandHelper;
import makeus.cmc.malmo.application.helper.chat_room.ChatRoomQueryHelper;
import makeus.cmc.malmo.application.helper.couple.CoupleCommandHelper;
import makeus.cmc.malmo.application.helper.couple.CoupleQueryHelper;
import makeus.cmc.malmo.application.helper.member.MemberCommandHelper;
import makeus.cmc.malmo.application.helper.member.MemberMemoryCommandHelper;
import makeus.cmc.malmo.application.helper.member.MemberQueryHelper;
import makeus.cmc.malmo.application.helper.notification.MemberNotificationCommandHelper;
import makeus.cmc.malmo.application.helper.question.CoupleQuestionCommandHelper;
import makeus.cmc.malmo.application.helper.question.CoupleQuestionQueryHelper;
import makeus.cmc.malmo.application.port.in.couple.CoupleLinkUseCase;
import makeus.cmc.malmo.application.port.in.couple.CoupleUnlinkUseCase;
import makeus.cmc.malmo.application.port.out.sse.SendSseEventPort;
import makeus.cmc.malmo.application.port.out.sse.ValidateSsePort;
import makeus.cmc.malmo.domain.model.couple.Couple;
import makeus.cmc.malmo.domain.model.member.Member;
import makeus.cmc.malmo.domain.model.question.CoupleQuestion;
import makeus.cmc.malmo.domain.model.question.MemberAnswer;
import makeus.cmc.malmo.domain.model.question.Question;
import makeus.cmc.malmo.domain.model.question.TempCoupleQuestion;
import makeus.cmc.malmo.domain.service.CoupleDomainService;
import makeus.cmc.malmo.domain.value.id.CoupleId;
import makeus.cmc.malmo.domain.value.id.InviteCodeValue;
import makeus.cmc.malmo.domain.value.id.MemberId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static makeus.cmc.malmo.application.port.out.sse.SendSseEventPort.SseEventType.COUPLE_CONNECTED;
import static makeus.cmc.malmo.application.port.out.sse.SendSseEventPort.SseEventType.COUPLE_DISCONNECTED;
import static makeus.cmc.malmo.util.GlobalConstants.FIRST_QUESTION_LEVEL;

@Service
@RequiredArgsConstructor
public class CoupleService implements CoupleLinkUseCase, CoupleUnlinkUseCase {

    private final CoupleQueryHelper coupleQueryHelper;
    private final CoupleCommandHelper coupleCommandHelper;
    private final CoupleDomainService coupleDomainService;

    private final CoupleQuestionQueryHelper coupleQuestionQueryHelper;
    private final CoupleQuestionCommandHelper coupleQuestionCommandHelper;

    private final MemberQueryHelper memberQueryHelper;

    private final SendSseEventPort sendSseEventPort;
    private final ValidateSsePort validateSsePort;

    private final ChatRoomQueryHelper chatRoomQueryHelper;
    private final ChatRoomCommandHelper chatRoomCommandHelper;

    private final MemberMemoryCommandHelper memberMemoryCommandHelper;
    private final MemberCommandHelper memberCommandHelper;
    private final MemberNotificationCommandHelper memberNotificationCommandHelper;

    @Override
    @CheckValidMember
    @Transactional
    public CoupleLinkResponse coupleLink(CoupleLinkCommand command) {
        Member member = memberQueryHelper.getMemberByIdOrThrow(MemberId.of(command.getUserId()));

        // 초대 코드 유효성 검사
        // 본인의 초대코드는 사용할 수 없음
        memberQueryHelper.validateOwnInviteCode(member.getInviteCode(), InviteCodeValue.of(command.getCoupleCode()));

        // 이미 본인이 커플인 사용자인지 확인
        if (member.isCoupleLinked()) {
            coupleQueryHelper.validateBrokenCouple(member.getCoupleId());
            // 과거 커플의 메모리 삭제
            memberMemoryCommandHelper.deleteCoupleMemberMemory(member.getCoupleId(), MemberId.of(member.getId()));
        }

        // 상대방이 이미 커플인 사용자인지 확인
        Member partner = memberQueryHelper.getMemberByInviteCodeOrThrow(InviteCodeValue.of(command.getCoupleCode()));
        if (partner.isCoupleLinked()) {
            coupleQueryHelper.validateBrokenCouple(partner.getCoupleId());
            // 과거 커플의 메모리 삭제
            memberMemoryCommandHelper.deleteCoupleMemberMemory(partner.getCoupleId(), MemberId.of(partner.getId()));
        }

        // 커플 생성 또는 재연결
        // 과거 두 사용자가 커플이었던 경우 재연결, 아니라면 새로 생성
        // V2: 커플 연동 시 startLoveDate를 당일로 초기화
        Couple couple = coupleQueryHelper.getCoupleByMemberAndPartnerId(MemberId.of(member.getId()), MemberId.of(partner.getId()))
                .filter(Couple::canRecover)
                .map(this::reconnectCouple)
                .orElseGet(() -> createNewCouple(
                        MemberId.of(member.getId()),
                        MemberId.of(partner.getId()),
                        LocalDate.now() // V2: 당일로 초기화
                ));

        // 사용자 커플 연결 처리
        member.linkCouple(CoupleId.of(couple.getId()));
        partner.linkCouple(CoupleId.of(couple.getId()));

        memberCommandHelper.saveMember(member);
        memberCommandHelper.saveMember(partner);

        activateCoupleFeatures(MemberId.of(member.getId()), MemberId.of(partner.getId()), couple);

        return CoupleLinkResponse.builder()
                .coupleId(couple.getId())
                .build();
    }

    @Override
    @CheckCoupleMember
    @Transactional
    public void coupleUnlink(CoupleUnlinkCommand command) {
        Member member = memberQueryHelper.getMemberByIdOrThrow(MemberId.of(command.getUserId()));
        Couple couple = coupleQueryHelper.getCoupleByIdOrThrow(member.getCoupleId());
        MemberId partnerId = couple.getOtherMemberId(MemberId.of(command.getUserId()));

        // 사용자의 커플 메모리 제거
        memberMemoryCommandHelper.deleteCoupleMemberMemory(member.getCoupleId(), MemberId.of(member.getId()));

        // 커플 해제 처리
        couple.unlink(MemberId.of(member.getId()),
                member.getNickname(),
                member.getLoveTypeCategory(),
                member.getAnxietyRate(),
                member.getAvoidanceRate());
        coupleCommandHelper.saveCouple(couple);

        member.unlinkCouple();
        memberCommandHelper.saveMember(member);

        // 상대방에게 커플 해지됨 알림
        if (validateSsePort.isMemberOnline(partnerId)) {
            sendSseEventPort.sendToMember(partnerId,
                    new SendSseEventPort.NotificationEvent(COUPLE_DISCONNECTED, couple.getId())
            );
        } else {
            // 상대방이 온라인이 아닐 경우 알림 생성
            memberNotificationCommandHelper.createAndSaveCoupleDisconnectedNotification(partnerId);
        }

    }

    private Couple reconnectCouple(Couple brokenCouple) {
        brokenCouple.recover();
        // 커플 멤버들의 메모리 복구
        memberMemoryCommandHelper.recoverMemberMemory(CoupleId.of(brokenCouple.getId()));
        memberMemoryCommandHelper.recoverMemberMemory(CoupleId.of(brokenCouple.getId()));

        return coupleCommandHelper.saveCouple(brokenCouple);
    }

    private Couple createNewCouple(MemberId userId, MemberId partnerId, LocalDate startLoveDate) {
        Couple initCouple = coupleDomainService.createCoupleByInviteCode(userId, partnerId, startLoveDate);
        Couple couple = coupleCommandHelper.saveCouple(initCouple);
        setupInitialCoupleQuestion(couple, userId, partnerId);
        return couple;
    }

    private void setupInitialCoupleQuestion(Couple couple, MemberId userId, MemberId partnerId) {
        // 초기 커플 질문 생성
        Question question = coupleQuestionQueryHelper.getQuestionByLevelOrThrow(FIRST_QUESTION_LEVEL);
        CoupleQuestion coupleQuestion = coupleQuestionCommandHelper.saveCoupleQuestion(
                CoupleQuestion.createCoupleQuestion(question, CoupleId.of(couple.getId()))
        );

        // 과거 질문이 있는 경우 임시 질문에서 답변을 가져와서 커플 질문으로 저장
        boolean memberAnswered = migrateAnswerFromTemp(userId, coupleQuestion);
        boolean partnerAnswered = migrateAnswerFromTemp(partnerId, coupleQuestion);

        // 커플 질문에 답변이 모두 완료되면 커플 질문 상태를 완료로 설정
        if (memberAnswered && partnerAnswered) {
            coupleQuestion.complete();
            coupleQuestionCommandHelper.saveCoupleQuestion(coupleQuestion);
        }
    }

    private boolean migrateAnswerFromTemp(MemberId memberId, CoupleQuestion coupleQuestion) {
        return coupleQuestionQueryHelper.getTempCoupleQuestion(memberId)
                .filter(TempCoupleQuestion::isAnswered)
                .map(tempQuestion -> {
                    MemberAnswer memberAnswer = coupleQuestion.createMemberAnswer(memberId, tempQuestion.getAnswer());
                    coupleQuestionCommandHelper.saveMemberAnswer(memberAnswer);

                    tempQuestion.usedForCoupleQuestion();
                    coupleQuestionCommandHelper.saveTempCoupleQuestion(tempQuestion);
                    return true;
                })
                .orElse(false);
    }

    private void activateCoupleFeatures(MemberId memberId, MemberId partnerId, Couple couple) {
        chatRoomQueryHelper.getPausedChatRoomByMemberId(memberId)
                        .ifPresent(
                                chatRoom -> {
                                    chatRoom.updateChatRoomStateNeedNextQuestion();
                                    chatRoomCommandHelper.saveChatRoom(chatRoom);
                                }
                        );
        chatRoomQueryHelper.getPausedChatRoomByMemberId(partnerId)
                .ifPresent(
                        chatRoom -> {
                            chatRoom.updateChatRoomStateNeedNextQuestion();
                            chatRoomCommandHelper.saveChatRoom(chatRoom);
                        }
                );

        if (validateSsePort.isMemberOnline(partnerId)) {
            sendSseEventPort.sendToMember(
                    partnerId,
                    new SendSseEventPort.NotificationEvent(COUPLE_CONNECTED, couple.getId())
            );
        } else {
            memberNotificationCommandHelper.createAndSaveCoupleConnectedNotification(partnerId);
        }

    }
}