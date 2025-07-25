package makeus.cmc.malmo.adaptor.out.persistence.repository;

import makeus.cmc.malmo.adaptor.out.persistence.entity.member.MemberEntity;
import makeus.cmc.malmo.adaptor.out.persistence.repository.custom.MemberRepositoryCustom;
import makeus.cmc.malmo.domain.value.type.Provider;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<MemberEntity, Long>, MemberRepositoryCustom {

    Optional<MemberEntity> findByProviderAndProviderId(Provider providerJpa, String providerId);

    @Query("select m from MemberEntity m where m.inviteCodeEntityValue.value = ?1 and m.memberState = 'ALIVE'")
    Optional<MemberEntity> findMemberEntityByInviteCode(String inviteCode);

}
