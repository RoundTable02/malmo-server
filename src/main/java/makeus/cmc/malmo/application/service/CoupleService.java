package makeus.cmc.malmo.application.service;

import lombok.RequiredArgsConstructor;
import makeus.cmc.malmo.adaptor.in.aop.CheckCoupleMember;
import makeus.cmc.malmo.adaptor.in.aop.CheckValidMember;
import makeus.cmc.malmo.application.port.in.CoupleLinkUseCase;
import makeus.cmc.malmo.application.port.in.CoupleUnlinkUseCase;
import makeus.cmc.malmo.application.port.out.SendSseEventPort;
import makeus.cmc.malmo.application.service.helper.couple.CoupleCommandHelper;
import makeus.cmc.malmo.application.service.helper.couple.CoupleQueryHelper;
import makeus.cmc.malmo.application.service.helper.member.MemberQueryHelper;
import makeus.cmc.malmo.application.service.helper.question.CoupleQuestionCommandHelper;
import makeus.cmc.malmo.application.service.helper.question.CoupleQuestionQueryHelper;
import makeus.cmc.malmo.domain.model.couple.Couple;
import makeus.cmc.malmo.domain.model.member.Member;
import makeus.cmc.malmo.domain.model.question.CoupleQuestion;
import makeus.cmc.malmo.domain.model.question.MemberAnswer;
import makeus.cmc.malmo.domain.model.question.Question;
import makeus.cmc.malmo.domain.model.question.TempCoupleQuestion;
import makeus.cmc.malmo.domain.service.ChatRoomDomainService;
import makeus.cmc.malmo.domain.service.CoupleDomainService;
import makeus.cmc.malmo.domain.value.id.CoupleId;
import makeus.cmc.malmo.domain.value.id.CoupleMemberId;
import makeus.cmc.malmo.domain.value.id.InviteCodeValue;
import makeus.cmc.malmo.domain.value.id.MemberId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static makeus.cmc.malmo.application.port.out.SendSseEventPort.SseEventType.COUPLE_CONNECTED;
import static makeus.cmc.malmo.domain.service.CoupleQuestionDomainService.FIRST_QUESTION_LEVEL;

@Service
@RequiredArgsConstructor
public class CoupleService implements CoupleLinkUseCase, CoupleUnlinkUseCase {

    private final CoupleQueryHelper coupleQueryHelper;
    private final CoupleCommandHelper coupleCommandHelper;
    private final CoupleDomainService coupleDomainService;

    private final ChatRoomDomainService chatRoomDomainService;

    private final CoupleQuestionQueryHelper coupleQuestionQueryHelper;
    private final CoupleQuestionCommandHelper coupleQuestionCommandHelper;

    private final MemberQueryHelper memberQueryHelper;

    private final SendSseEventPort sendSseEventPort;

    @Override
    @CheckValidMember
    @Transactional
    public CoupleLinkResponse coupleLink(CoupleLinkCommand command) {
        // 유효성 검사 및 파트너 조회
        validateCoupleLinkRequest(command);
        Member partner = memberQueryHelper.getMemberByInviteCodeOrThrow(InviteCodeValue.of(command.getCoupleCode()));
        MemberId userId = MemberId.of(command.getUserId());
        MemberId partnerId = MemberId.of(partner.getId());

        // 커플 조회 또는 생성
        Couple couple = coupleQueryHelper.getBrokenCouple(userId, partnerId)
                .map(this::reconnectCouple)
                .orElseGet(() -> createNewCouple(userId, partnerId, partner.getStartLoveDate()));

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
        couple.delete();
        coupleCommandHelper.saveCouple(couple);
    }

    private void validateCoupleLinkRequest(CoupleLinkCommand command) {
        InviteCodeValue inviteCode = InviteCodeValue.of(command.getCoupleCode());
        MemberId userId = MemberId.of(command.getUserId());
        memberQueryHelper.validateUsedInviteCode(inviteCode);
        memberQueryHelper.validateMemberNotCoupled(userId);
        memberQueryHelper.validateOwnInviteCode(userId, inviteCode);
    }

    private Couple reconnectCouple(Couple brokenCouple) {
        brokenCouple.recover();
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

    private void activateCoupleFeatures(MemberId userId, MemberId partnerId, Couple couple) {
        chatRoomDomainService.updateMemberPausedChatRoomStateToAlive(userId);
        chatRoomDomainService.updateMemberPausedChatRoomStateToAlive(partnerId);

        sendSseEventPort.sendToMember(
                partnerId,
                new SendSseEventPort.NotificationEvent(COUPLE_CONNECTED, couple.getId())
        );
    }
}