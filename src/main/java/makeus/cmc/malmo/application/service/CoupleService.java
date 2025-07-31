package makeus.cmc.malmo.application.service;

import lombok.RequiredArgsConstructor;
import makeus.cmc.malmo.adaptor.in.aop.CheckCoupleMember;
import makeus.cmc.malmo.adaptor.in.aop.CheckValidMember;
import makeus.cmc.malmo.application.port.in.CoupleLinkUseCase;
import makeus.cmc.malmo.application.port.in.CoupleUnlinkUseCase;
import makeus.cmc.malmo.application.port.out.SaveCouplePort;
import makeus.cmc.malmo.application.port.out.SendSseEventPort;
import makeus.cmc.malmo.application.service.helper.couple.CoupleCommandHelper;
import makeus.cmc.malmo.application.service.helper.couple.CoupleQueryHelper;
import makeus.cmc.malmo.application.service.helper.question.CoupleQuestionCommandHelper;
import makeus.cmc.malmo.application.service.helper.question.CoupleQuestionQueryHelper;
import makeus.cmc.malmo.domain.exception.QuestionNotFoundException;
import makeus.cmc.malmo.domain.model.couple.Couple;
import makeus.cmc.malmo.domain.model.member.Member;
import makeus.cmc.malmo.domain.model.question.CoupleQuestion;
import makeus.cmc.malmo.domain.model.question.MemberAnswer;
import makeus.cmc.malmo.domain.model.question.Question;
import makeus.cmc.malmo.domain.model.question.TempCoupleQuestion;
import makeus.cmc.malmo.domain.service.ChatRoomDomainService;
import makeus.cmc.malmo.domain.service.CoupleDomainService;
import makeus.cmc.malmo.domain.service.CoupleQuestionDomainService;
import makeus.cmc.malmo.domain.service.InviteCodeDomainService;
import makeus.cmc.malmo.domain.value.id.CoupleId;
import makeus.cmc.malmo.domain.value.id.CoupleMemberId;
import makeus.cmc.malmo.domain.value.id.InviteCodeValue;
import makeus.cmc.malmo.domain.value.id.MemberId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static makeus.cmc.malmo.application.port.out.SendSseEventPort.SseEventType.COUPLE_CONNECTED;
import static makeus.cmc.malmo.domain.service.CoupleQuestionDomainService.FIRST_QUESTION_LEVEL;

@Service
@RequiredArgsConstructor
public class CoupleService implements CoupleLinkUseCase, CoupleUnlinkUseCase {

    private final InviteCodeDomainService inviteCodeDomainService;
    private final CoupleDomainService coupleDomainService;
    private final CoupleCommandHelper coupleCommandHelper;
    private final CoupleQueryHelper coupleQueryHelper;
    private final ChatRoomDomainService chatRoomDomainService;

    private final CoupleQuestionDomainService coupleQuestionDomainService;

    private final SendSseEventPort sendSseEventPort;

    private final SaveCouplePort saveCouplePort;
    private final CoupleQuestionQueryHelper coupleQuestionQueryHelper;
    private final CoupleQuestionCommandHelper coupleQuestionCommandHelper;

    @Override
    @CheckValidMember
    @Transactional
    public CoupleLinkResponse coupleLink(CoupleLinkCommand command) {
        InviteCodeValue inviteCode = InviteCodeValue.of(command.getCoupleCode());
        inviteCodeDomainService.validateUsedInviteCode(inviteCode);
        inviteCodeDomainService.validateMemberNotCoupled(MemberId.of(command.getUserId()));
        inviteCodeDomainService.validateOwnInviteCode(MemberId.of(command.getUserId()), inviteCode);

        Member partner = inviteCodeDomainService.getMemberByInviteCode(inviteCode);

        Optional<Couple> brokenCouple = coupleQueryHelper.getBrokenCouple(MemberId.of(command.getUserId()), MemberId.of(partner.getId()));

        Couple couple;
        if (brokenCouple.isPresent()) {
            couple = brokenCouple.map(bc ->{
                bc.recover();
                return saveCouplePort.saveCouple(bc);
            }).get();
        }
        else {
            Couple initCouple = coupleDomainService.createCoupleByInviteCode(
                    MemberId.of(command.getUserId()),
                    MemberId.of(partner.getId()),
                    partner.getStartLoveDate());

            couple = saveCouplePort.saveCouple(initCouple);

            Question question = coupleQuestionQueryHelper.getQuestionByLevelOrThrow(FIRST_QUESTION_LEVEL);

            CoupleMemberId coupleMemberId = coupleQueryHelper.getCoupleMemberIdByMemberId(MemberId.of(command.getUserId()));
            CoupleMemberId couplePartnerId = coupleQueryHelper.getCoupleMemberIdByMemberId(MemberId.of(partner.getId()));

            CoupleQuestion coupleQuestion = CoupleQuestion.createCoupleQuestion(question, CoupleId.of(couple.getId()));
            CoupleQuestion savedCoupleQuestion = coupleQuestionCommandHelper.saveCoupleQuestion(coupleQuestion);

            boolean memberAnswered = coupleQuestionQueryHelper.getTempCoupleQuestion(MemberId.of(command.getUserId()))
                    .filter(TempCoupleQuestion::isAnswered)
                    .map(tempCoupleQuestion -> {
                        // 이미 질문에
                        tempCoupleQuestion.usedForCoupleQuestion();
                        coupleQuestionCommandHelper.saveTempCoupleQuestion(tempCoupleQuestion);
                        MemberAnswer memberAnswer = savedCoupleQuestion.createMemberAnswer(coupleMemberId, tempCoupleQuestion.getAnswer());
                        coupleQuestionCommandHelper.saveMemberAnswer(memberAnswer);
                        return true;
                    })
                    .orElse(false);

            boolean partnerAnswered = coupleQuestionQueryHelper.getTempCoupleQuestion(MemberId.of(partner.getId()))
                    .filter(TempCoupleQuestion::isAnswered)
                    .map(tempCoupleQuestion -> {
                        tempCoupleQuestion.usedForCoupleQuestion();
                        coupleQuestionCommandHelper.saveTempCoupleQuestion(tempCoupleQuestion);
                        MemberAnswer memberAnswer = savedCoupleQuestion.createMemberAnswer(couplePartnerId, tempCoupleQuestion.getAnswer());
                        coupleQuestionCommandHelper.saveMemberAnswer(memberAnswer);
                        return true;
                    })
                    .orElse(false);

            if (memberAnswered && partnerAnswered) {
                savedCoupleQuestion.complete();
            }

            coupleQuestionCommandHelper.saveCoupleQuestion(savedCoupleQuestion);
        }

        // 커플 연결 전 일시 정지 상태의 채팅방을 활성화 (나 & 상대방)
        chatRoomDomainService.updateMemberPausedChatRoomStateToAlive(MemberId.of(command.getUserId()));
        chatRoomDomainService.updateMemberPausedChatRoomStateToAlive(MemberId.of(partner.getId()));

        sendSseEventPort.sendToMember(
                MemberId.of(partner.getId()),
                new SendSseEventPort.NotificationEvent(
                        COUPLE_CONNECTED, couple.getId())
        );

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
}
