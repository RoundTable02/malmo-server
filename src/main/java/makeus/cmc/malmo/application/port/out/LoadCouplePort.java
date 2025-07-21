package makeus.cmc.malmo.application.port.out;

import makeus.cmc.malmo.domain.value.id.CoupleId;
import makeus.cmc.malmo.domain.value.id.CoupleMemberId;
import makeus.cmc.malmo.domain.value.id.MemberId;

public interface LoadCouplePort {
    CoupleMemberId loadCoupleMemberIdByMemberId(MemberId memberId);
}
