package makeus.cmc.malmo.application.port.out;

import makeus.cmc.malmo.domain.model.member.Member;
import makeus.cmc.malmo.domain.model.member.Provider;

import java.util.Optional;

public interface LoadMemberPort {
    Optional<Member> loadMemberByProviderId(Provider providerJpa, String providerId);
    Optional<Member> loadMemberById(Long memberId);
}
