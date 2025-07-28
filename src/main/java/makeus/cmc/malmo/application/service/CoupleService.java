package makeus.cmc.malmo.application.service;

import lombok.RequiredArgsConstructor;
import makeus.cmc.malmo.adaptor.in.aop.CheckCoupleMember;
import makeus.cmc.malmo.adaptor.in.aop.CheckValidMember;
import makeus.cmc.malmo.application.port.in.CoupleLinkUseCase;
import makeus.cmc.malmo.application.port.in.CoupleUnlinkUseCase;
import makeus.cmc.malmo.application.port.out.SaveCouplePort;
import makeus.cmc.malmo.application.port.out.SendSseEventPort;
import makeus.cmc.malmo.domain.model.couple.Couple;
import makeus.cmc.malmo.domain.model.member.Member;
import makeus.cmc.malmo.domain.service.ChatRoomDomainService;
import makeus.cmc.malmo.domain.service.CoupleDomainService;
import makeus.cmc.malmo.domain.service.CoupleQuestionDomainService;
import makeus.cmc.malmo.domain.service.InviteCodeDomainService;
import makeus.cmc.malmo.domain.value.id.CoupleId;
import makeus.cmc.malmo.domain.value.id.InviteCodeValue;
import makeus.cmc.malmo.domain.value.id.MemberId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static makeus.cmc.malmo.application.port.out.SendSseEventPort.SseEventType.COUPLE_CONNECTED;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CoupleService implements CoupleLinkUseCase, CoupleUnlinkUseCase {

    private final InviteCodeDomainService inviteCodeDomainService;
    private final CoupleDomainService coupleDomainService;
    private final ChatRoomDomainService chatRoomDomainService;

    private final CoupleQuestionDomainService coupleQuestionDomainService;

    private final SendSseEventPort sendSseEventPort;

    private final SaveCouplePort saveCouplePort;

    @Override
    @Transactional
    @CheckValidMember
    public CoupleLinkResponse coupleLink(CoupleLinkCommand command) {
        InviteCodeValue inviteCode = InviteCodeValue.of(command.getCoupleCode());
        inviteCodeDomainService.validateUsedInviteCode(inviteCode);
        inviteCodeDomainService.validateMemberNotCoupled(MemberId.of(command.getUserId()));
        inviteCodeDomainService.validateOwnInviteCode(MemberId.of(command.getUserId()), inviteCode);

        Member partner = inviteCodeDomainService.getMemberByInviteCode(inviteCode);

        Optional<Couple> brokenCouple = coupleDomainService.getBrokenCouple(MemberId.of(command.getUserId()), MemberId.of(partner.getId()));

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

            coupleQuestionDomainService.createFirstCoupleQuestion(
                    CoupleId.of(couple.getId()),
                    MemberId.of(command.getUserId()),
                    MemberId.of(partner.getId())
            );
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
    @Transactional
    @CheckCoupleMember
    public void coupleUnlink(CoupleUnlinkCommand command) {
        coupleDomainService.deleteCoupleByMemberId(MemberId.of(command.getUserId()));
    }
}
