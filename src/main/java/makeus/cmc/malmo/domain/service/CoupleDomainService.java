package makeus.cmc.malmo.domain.service;

import lombok.RequiredArgsConstructor;
import makeus.cmc.malmo.domain.model.couple.Couple;
import makeus.cmc.malmo.domain.model.couple.CoupleState;
import makeus.cmc.malmo.domain.model.value.MemberId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CoupleDomainService {

    public Couple createCoupleByInviteCode(MemberId memberId, MemberId partnerId, LocalDate startLoveDate) {
        return Couple.createCouple(
                memberId.getValue(),
                partnerId.getValue(),
                startLoveDate,
                CoupleState.ALIVE
        );
    }

}
