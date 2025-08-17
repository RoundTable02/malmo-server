package makeus.cmc.malmo.application.service.couple;

import lombok.RequiredArgsConstructor;
import makeus.cmc.malmo.adaptor.in.aop.CheckCoupleMember;
import makeus.cmc.malmo.adaptor.in.aop.CheckValidMember;
import makeus.cmc.malmo.application.helper.chat_room.ChatRoomCommandHelper;
import makeus.cmc.malmo.application.helper.chat_room.ChatRoomQueryHelper;
import makeus.cmc.malmo.application.helper.couple.CoupleCommandHelper;
import makeus.cmc.malmo.application.helper.couple.CoupleQueryHelper;
import makeus.cmc.malmo.application.helper.member.MemberMemoryCommandHelper;
import makeus.cmc.malmo.application.helper.member.MemberQueryHelper;
import makeus.cmc.malmo.application.helper.question.CoupleQuestionCommandHelper;
import makeus.cmc.malmo.application.helper.question.CoupleQuestionQueryHelper;
import makeus.cmc.malmo.application.port.in.couple.CoupleLinkUseCase;
import makeus.cmc.malmo.application.port.in.couple.CoupleUnlinkUseCase;
import makeus.cmc.malmo.application.port.out.SendSseEventPort;
import makeus.cmc.malmo.application.port.out.member.ValidateMemberPort;
import makeus.cmc.malmo.domain.model.couple.Couple;
import makeus.cmc.malmo.domain.model.member.Member;
import makeus.cmc.malmo.domain.model.question.CoupleQuestion;
import makeus.cmc.malmo.domain.model.question.MemberAnswer;
import makeus.cmc.malmo.domain.model.question.Question;
import makeus.cmc.malmo.domain.model.question.TempCoupleQuestion;
import makeus.cmc.malmo.domain.service.CoupleDomainService;
import makeus.cmc.malmo.domain.value.id.CoupleId;
import makeus.cmc.malmo.domain.value.id.CoupleMemberId;
import makeus.cmc.malmo.domain.value.id.InviteCodeValue;
import makeus.cmc.malmo.domain.value.id.MemberId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static makeus.cmc.malmo.application.port.out.SendSseEventPort.SseEventType.COUPLE_CONNECTED;
import static makeus.cmc.malmo.application.port.out.SendSseEventPort.SseEventType.COUPLE_DISCONNECTED;
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
    private final ChatRoomQueryHelper chatRoomQueryHelper;
    private final ChatRoomCommandHelper chatRoomCommandHelper;

    private final MemberMemoryCommandHelper memberMemoryCommandHelper;

    @Override
    @CheckValidMember
    @Transactional
    public CoupleLinkResponse coupleLink(CoupleLinkCommand command) {
        // 초대코드 주인 조회
        Member partner = memberQueryHelper.getMemberByInviteCodeOrThrow(InviteCodeValue.of(command.getCoupleCode()));

        // 유효성 검사
        validateCoupleLinkRequest(MemberId.of(command.getUserId()),
                MemberId.of(partner.getId()),
                InviteCodeValue.of(command.getCoupleCode()));

        MemberId userId = MemberId.of(command.getUserId());
        MemberId partnerId = MemberId.of(partner.getId());

        // 이전 커플 연결에서 생성된 메모리 삭제
        memberMemoryCommandHelper.deleteAliveMemory(MemberId.of(command.getUserId()));

        // 이전 상대가 연결 해제, 본인은 따로 해지하지 않은 상황
        coupleQueryHelper.getCoupleByMemberId(userId)
                .ifPresent(couple -> {
                    // 연결 해제 처리
                    couple.unlink(userId);
                    coupleCommandHelper.saveCouple(couple);
                });

        // 이전에 생성되었던 커플이 있는지 조회, 없으면 생성
        Couple couple = coupleQueryHelper.getBrokenCouple(userId, partnerId)
                .map(this::reconnectCouple)
                .orElseGet(() -> {
                    // 새로운 커플 생성
                    return createNewCouple(userId, partnerId, partner.getStartLoveDate());
                });

        // 커플 연결 후 부가 기능 활성화
        activateCoupleFeatures(userId, partnerId, couple);

        return CoupleLinkResponse.builder()
                .coupleId(couple.getId())
                .build();
    }

    @Override
    @CheckCoupleMember
    @Transactional
    public void coupleUnlink(CoupleUnlinkCommand command) {
        Couple couple = coupleQueryHelper.getCoupleByMemberIdOrThrow(MemberId.of(command.getUserId()));
        couple.unlink(MemberId.of(command.getUserId()));
        coupleCommandHelper.saveCouple(couple);
        sendSseEventPort.sendToMember(MemberId.of(command.getUserId()),
                new SendSseEventPort.NotificationEvent(COUPLE_DISCONNECTED, couple.getId())
        );
    }

    private void validateCoupleLinkRequest(MemberId userId, MemberId codeOwnerId, InviteCodeValue inviteCode) {
        memberQueryHelper.validateUsedInviteCode(codeOwnerId);
        memberQueryHelper.validateMemberNotCoupled(userId);
        memberQueryHelper.validateOwnInviteCode(userId, inviteCode);
    }

    private Couple reconnectCouple(Couple brokenCouple) {
        brokenCouple.recover();
        // 커플 멤버들의 메모리 복구
        brokenCouple.getCoupleMembers().forEach(cm -> {
            CoupleMemberId coupleMemberId = CoupleMemberId.of(cm.getId());
            memberMemoryCommandHelper.recoverMemberMemory(coupleMemberId);
        });
        return coupleCommandHelper.saveCouple(brokenCouple);
    }

    private Couple createNewCouple(MemberId userId, MemberId partnerId, LocalDate startLoveDate) {
        Couple initCouple = coupleDomainService.createCoupleByInviteCode(userId, partnerId, startLoveDate);
        Couple couple = coupleCommandHelper.saveCouple(initCouple);
        setupInitialCoupleQuestion(couple, userId, partnerId);
        return couple;
    }

    private void setupInitialCoupleQuestion(Couple couple, MemberId userId, MemberId partnerId) {
        Question question = coupleQuestionQueryHelper.getQuestionByLevelOrThrow(FIRST_QUESTION_LEVEL);
        CoupleQuestion coupleQuestion = coupleQuestionCommandHelper.saveCoupleQuestion(
                CoupleQuestion.createCoupleQuestion(question, CoupleId.of(couple.getId()))
        );

        boolean memberAnswered = migrateAnswerFromTemp(userId, coupleQuestion);
        boolean partnerAnswered = migrateAnswerFromTemp(partnerId, coupleQuestion);

        if (memberAnswered && partnerAnswered) {
            coupleQuestion.complete();
            coupleQuestionCommandHelper.saveCoupleQuestion(coupleQuestion);
        }
    }

    private boolean migrateAnswerFromTemp(MemberId memberId, CoupleQuestion coupleQuestion) {
        return coupleQuestionQueryHelper.getTempCoupleQuestion(memberId)
                .filter(TempCoupleQuestion::isAnswered)
                .map(tempQuestion -> {
                    CoupleMemberId coupleMemberId = coupleQueryHelper.getCoupleMemberIdByMemberId(memberId);
                    MemberAnswer memberAnswer = coupleQuestion.createMemberAnswer(coupleMemberId, tempQuestion.getAnswer());
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

        sendSseEventPort.sendToMember(
                partnerId,
                new SendSseEventPort.NotificationEvent(COUPLE_CONNECTED, couple.getId())
        );
    }
}