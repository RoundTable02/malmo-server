package makeus.cmc.malmo.domain.service;

import lombok.RequiredArgsConstructor;
import makeus.cmc.malmo.application.port.out.LoadCoupleCodePort;
import makeus.cmc.malmo.application.port.out.SaveCoupleCodePort;
import makeus.cmc.malmo.domain.exception.CoupleCodeNotFoundException;
import makeus.cmc.malmo.domain.model.couple.Couple;
import makeus.cmc.malmo.domain.model.couple.CoupleState;
import makeus.cmc.malmo.domain.model.member.CoupleCode;
import makeus.cmc.malmo.domain.model.member.Member;
import makeus.cmc.malmo.domain.model.value.MemberId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CoupleDomainService {

    private final LoadCoupleCodePort loadCoupleCodePort;
    private final SaveCoupleCodePort saveCoupleCodePort;

    @Transactional
    public Couple createCoupleByCoupleCode(Member member, CoupleCode coupleCode) {
        CoupleCode memberCoupleCode = loadCoupleCodePort.loadCoupleCodeByMemberId(MemberId.of(member.getId()))
                .orElseThrow(CoupleCodeNotFoundException::new);

        expireCoupleCode(memberCoupleCode);
        expireCoupleCode(coupleCode);

        return Couple.createCouple(
                member.getId(),
                coupleCode.getMemberId().getValue(),
                coupleCode.getStartLoveDate(),
                CoupleState.ALIVE
        );
    }

    private void expireCoupleCode(CoupleCode coupleCode) {
        coupleCode.expire();
        saveCoupleCodePort.saveCoupleCode(coupleCode);
    }

}
