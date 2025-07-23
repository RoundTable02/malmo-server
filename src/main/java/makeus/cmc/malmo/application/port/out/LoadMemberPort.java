package makeus.cmc.malmo.application.port.out;

import lombok.AllArgsConstructor;
import lombok.Data;
import makeus.cmc.malmo.adaptor.out.persistence.MemberPersistenceAdapter;
import makeus.cmc.malmo.application.service.MemberInfoService;
import makeus.cmc.malmo.domain.model.member.Member;
import makeus.cmc.malmo.domain.value.id.InviteCodeValue;
import makeus.cmc.malmo.domain.value.id.MemberId;
import makeus.cmc.malmo.domain.value.type.LoveTypeCategory;
import makeus.cmc.malmo.domain.value.type.Provider;

import java.time.LocalDate;
import java.util.Optional;

public interface LoadMemberPort {
    Optional<Member> loadMemberByProviderId(Provider providerJpa, String providerId);
    Optional<Member> loadMemberById(MemberId memberId);
    Optional<MemberInfoService.MemberInfoDto> loadMemberDetailsById(MemberId memberId);
    Optional<Member> loadMemberByInviteCode(InviteCodeValue inviteCode);
}
