package makeus.cmc.malmo.application.service.helper.couple;

import lombok.RequiredArgsConstructor;
import makeus.cmc.malmo.application.port.out.LoadCouplePort;
import makeus.cmc.malmo.domain.exception.NotCoupleMemberException;
import makeus.cmc.malmo.domain.model.couple.Couple;
import makeus.cmc.malmo.domain.value.id.CoupleId;
import makeus.cmc.malmo.domain.value.id.CoupleMemberId;
import makeus.cmc.malmo.domain.value.id.MemberId;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class CoupleQueryHelper {

    private final LoadCouplePort loadCouplePort;

    public CoupleId getCoupleIdByMemberId(MemberId memberId) {
        return loadCouplePort.loadCoupleIdByMemberId(memberId);
    }

    public CoupleMemberId getCoupleMemberIdByMemberId(MemberId memberId) {
        return loadCouplePort.loadCoupleMemberIdByMemberId(memberId);
    }

    public Couple getCoupleByMemberIdOrThrow(MemberId memberId) {
        return loadCouplePort.loadCoupleByMemberId(memberId)
                .orElseThrow(NotCoupleMemberException::new);
    }

    public Optional<Couple> getCoupleByMemberId(MemberId memberId) {
        return loadCouplePort.loadCoupleByMemberId(memberId);
    }

    public Optional<Couple> getBrokenCouple(MemberId memberId, MemberId partnerId) {
        return loadCouplePort.loadCoupleByMemberIdAndPartnerId(memberId, partnerId);
    }
}
