package makeus.cmc.malmo.application.service;

import lombok.RequiredArgsConstructor;
import makeus.cmc.malmo.application.port.in.CoupleLinkUseCase;
import makeus.cmc.malmo.application.port.out.SaveCouplePort;
import makeus.cmc.malmo.domain.model.couple.Couple;
import makeus.cmc.malmo.domain.model.member.CoupleCode;
import makeus.cmc.malmo.domain.model.member.Member;
import makeus.cmc.malmo.domain.model.value.MemberId;
import makeus.cmc.malmo.domain.service.CoupleCodeDomainService;
import makeus.cmc.malmo.domain.service.CoupleDomainService;
import makeus.cmc.malmo.domain.service.MemberDomainService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CoupleService implements CoupleLinkUseCase {

    private final MemberDomainService memberDomainService;
    private final CoupleCodeDomainService coupleCodeDomainService;
    private final CoupleDomainService coupleDomainService;

    private final SaveCouplePort saveCouplePort;

    @Override
    @Transactional
    public CoupleLinkResponse coupleLink(CoupleLinkCommand command) {
        Member member = memberDomainService.getMemberById(MemberId.of(command.getUserId()));
        CoupleCode coupleCode = coupleCodeDomainService.getCoupleCodeByInviteCode(command.getCoupleCode());

        Couple couple = coupleDomainService.createCoupleByCoupleCode(member, coupleCode);

        Couple savedCouple = saveCouplePort.saveCouple(couple);

        return CoupleLinkResponse.builder()
                .coupleId(savedCouple.getId())
                .build();
    }
}
