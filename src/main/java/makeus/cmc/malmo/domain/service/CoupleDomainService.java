package makeus.cmc.malmo.domain.service;

import lombok.RequiredArgsConstructor;
import makeus.cmc.malmo.application.port.out.LoadCouplePort;
import makeus.cmc.malmo.application.port.out.SaveCouplePort;
import makeus.cmc.malmo.domain.model.couple.Couple;
import makeus.cmc.malmo.domain.value.id.CoupleId;
import makeus.cmc.malmo.domain.value.id.MemberId;
import makeus.cmc.malmo.domain.value.state.CoupleState;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CoupleDomainService {

    private final LoadCouplePort loadCouplePort;
    private final SaveCouplePort saveCouplePort;

    public Couple createCoupleByInviteCode(MemberId memberId, MemberId partnerId, LocalDate startLoveDate) {
        return Couple.createCouple(
                memberId.getValue(),
                partnerId.getValue(),
                startLoveDate,
                CoupleState.ALIVE
        );
    }

    public CoupleId getCoupleIdByMemberId(MemberId memberId) {
        return loadCouplePort.loadCoupleIdByMemberId(memberId);
    }

    @Transactional
    public void deleteCoupleByMemberId(MemberId memberId) {
        loadCouplePort.loadCoupleByMemberId(memberId)
                .ifPresent(couple -> {
                    couple.delete();
                    saveCouplePort.saveCouple(couple);
                });
    }

    public Optional<Couple> getBrokenCouple(MemberId memberId, MemberId partnerId) {
        return loadCouplePort.loadCoupleByMemberIdAndPartnerId(memberId, partnerId);
    }
}
