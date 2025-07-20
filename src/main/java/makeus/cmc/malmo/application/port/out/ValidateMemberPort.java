package makeus.cmc.malmo.application.port.out;

import makeus.cmc.malmo.domain.value.id.MemberId;

public interface ValidateMemberPort {
    boolean isCoupleMember(MemberId memberId);

    boolean isTestedMember(MemberId memberId);
}
