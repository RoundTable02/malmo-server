package makeus.cmc.malmo.domain.service;

import lombok.RequiredArgsConstructor;
import makeus.cmc.malmo.domain.model.couple.Couple;
import makeus.cmc.malmo.domain.value.id.CoupleId;
import makeus.cmc.malmo.domain.value.id.MemberId;
import makeus.cmc.malmo.domain.value.state.CoupleState;
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

    public CoupleId getCoupleIdByMemberId(MemberId memberId) {
        return null;
    }

}
