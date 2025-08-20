package makeus.cmc.malmo.application.port.out.member;

import makeus.cmc.malmo.domain.value.id.MemberId;

public interface ValidateMemberPort {
    boolean isCoupleMember(MemberId memberId);

    boolean isValidMember(MemberId memberId);

}
