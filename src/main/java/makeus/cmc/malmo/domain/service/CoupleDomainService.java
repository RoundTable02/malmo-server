package makeus.cmc.malmo.domain.service;

import makeus.cmc.malmo.domain.model.couple.Couple;
import makeus.cmc.malmo.domain.value.id.MemberId;
import makeus.cmc.malmo.domain.value.state.CoupleState;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
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
