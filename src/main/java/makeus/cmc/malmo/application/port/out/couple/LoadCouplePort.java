package makeus.cmc.malmo.application.port.out.couple;

import makeus.cmc.malmo.domain.model.couple.Couple;
import makeus.cmc.malmo.domain.value.id.CoupleId;
import makeus.cmc.malmo.domain.value.id.CoupleMemberId;
import makeus.cmc.malmo.domain.value.id.MemberId;

import java.util.Optional;

public interface LoadCouplePort {
    CoupleId loadCoupleIdByMemberId(MemberId memberId);
    CoupleMemberId loadCoupleMemberIdByMemberId(MemberId memberId);
    Optional<Couple> loadCoupleByMemberId(MemberId memberId);
    Optional<Couple> loadCoupleByMemberIdAndPartnerId(MemberId memberId, MemberId partnerId);
}
