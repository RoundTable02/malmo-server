package makeus.cmc.malmo.application.port.out.member;

import makeus.cmc.malmo.application.helper.member.MemberQueryHelper;
import makeus.cmc.malmo.domain.value.id.MemberId;

import java.util.Optional;

public interface LoadPartnerPort {
    Optional<MemberQueryHelper.PartnerMemberDto> loadPartnerByMemberId(MemberId memberId);
}
