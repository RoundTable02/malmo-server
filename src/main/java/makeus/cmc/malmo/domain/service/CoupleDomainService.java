package makeus.cmc.malmo.domain.service;

import lombok.RequiredArgsConstructor;
import makeus.cmc.malmo.domain.model.couple.Couple;
import makeus.cmc.malmo.domain.model.couple.CoupleState;
import makeus.cmc.malmo.domain.model.member.CoupleCode;
import makeus.cmc.malmo.domain.model.member.Member;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CoupleDomainService {

    public Couple createCouple(Member member, CoupleCode coupleCode) {
        return Couple.createCouple(
                member.getId(),
                coupleCode.getMemberId().getValue(),
                coupleCode.getStartLoveDate(),
                CoupleState.ALIVE
        );
    }

}
