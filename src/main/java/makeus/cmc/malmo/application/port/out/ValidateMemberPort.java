package makeus.cmc.malmo.application.port.out;

import makeus.cmc.malmo.domain.model.value.MemberId;

public interface ValidateMemberPort {
    boolean isCoupleMember(MemberId memberId);
}
