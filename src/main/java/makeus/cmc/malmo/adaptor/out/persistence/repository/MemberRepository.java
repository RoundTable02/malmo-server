package makeus.cmc.malmo.adaptor.out.persistence.repository;

import makeus.cmc.malmo.adaptor.out.persistence.entity.member.MemberEntity;
import makeus.cmc.malmo.adaptor.out.persistence.entity.member.ProviderJpa;
import makeus.cmc.malmo.adaptor.out.persistence.repository.custom.MemberRepositoryCustom;
import makeus.cmc.malmo.domain.model.love_type.LoveTypeCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<MemberEntity, Long>, MemberRepositoryCustom {

    Optional<MemberEntity> findByProviderJpaAndProviderId(ProviderJpa providerJpa, String providerId);

    @Query("select m from MemberEntity m where m.inviteCodeEntityValue.value = ?1 and m.memberStateJpa = 'ALIVE'")
    Optional<MemberEntity> findMemberEntityByInviteCode(String inviteCode);

    @Query("select m.loveTypeCategory from MemberEntity m where m.id = ?1 and m.memberStateJpa = 'ALIVE'")
    Optional<LoveTypeCategory> findLoveTypeCategoryByMemberId(Long memberId);
}
