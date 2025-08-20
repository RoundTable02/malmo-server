package makeus.cmc.malmo.adaptor.out.persistence.repository.member;

import makeus.cmc.malmo.adaptor.out.persistence.entity.member.MemberEntity;
import makeus.cmc.malmo.domain.value.type.Provider;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<MemberEntity, Long>, MemberRepositoryCustom {

    @Query("select m from MemberEntity m where m.id = ?1 and m.memberState != 'DELETED'")
    Optional<MemberEntity> findById(Long memberId);

    Optional<MemberEntity> findByProviderAndProviderId(Provider providerJpa, String providerId);

    @Query("select m from MemberEntity m where m.inviteCodeEntityValue.value = ?1 and m.memberState = 'ALIVE'")
    Optional<MemberEntity> findMemberEntityByInviteCode(String inviteCode);

    @Query("select m.coupleEntityId.value from MemberEntity m where m.id = ?1 and m.memberState != 'DELETED'")
    Long findCoupleIdByMemberId(Long memberId);

}
