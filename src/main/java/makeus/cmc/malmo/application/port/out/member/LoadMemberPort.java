package makeus.cmc.malmo.application.port.out.member;

import makeus.cmc.malmo.application.helper.member.MemberQueryHelper;
import makeus.cmc.malmo.domain.model.member.Member;
import makeus.cmc.malmo.domain.value.id.CoupleId;
import makeus.cmc.malmo.domain.value.id.InviteCodeValue;
import makeus.cmc.malmo.domain.value.id.MemberId;
import makeus.cmc.malmo.domain.value.type.Provider;

import java.util.Optional;

public interface LoadMemberPort {
    CoupleId loadCoupleIdByMemberId(MemberId memberId);
    Optional<Member> loadMemberByProviderId(Provider providerJpa, String providerId);
    Optional<Member> loadMemberById(MemberId memberId);
    Optional<MemberQueryHelper.MemberInfoDto> loadMemberDetailsById(MemberId memberId);
    Optional<Member> loadMemberByInviteCode(InviteCodeValue inviteCode);
}
