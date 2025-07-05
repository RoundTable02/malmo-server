package makeus.cmc.malmo.adaptor.out.persistence.repository;

import makeus.cmc.malmo.adaptor.out.persistence.entity.member.MemberEntity;
import makeus.cmc.malmo.adaptor.out.persistence.entity.member.ProviderJpa;
import makeus.cmc.malmo.adaptor.out.persistence.repository.custom.MemberRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<MemberEntity, Long>, MemberRepositoryCustom {

    Optional<MemberEntity> findByProviderJpaAndProviderId(ProviderJpa providerJpa, String providerId);
}
