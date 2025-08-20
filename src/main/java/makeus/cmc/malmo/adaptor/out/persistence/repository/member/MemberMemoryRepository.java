package makeus.cmc.malmo.adaptor.out.persistence.repository.member;

import makeus.cmc.malmo.adaptor.out.persistence.entity.member.MemberMemoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface MemberMemoryRepository extends JpaRepository<MemberMemoryEntity, Long> {

    @Query("select m from MemberMemoryEntity m where m.memberEntityId.value = ?1 " +
            "and m.memberMemoryState = 'ALIVE'")
    List<MemberMemoryEntity> findByMemberEntityId(Long memberId);

    @Modifying
    @Transactional
    @Query("update MemberMemoryEntity m set m.memberMemoryState = 'DELETED', m.deletedAt = CURRENT_TIMESTAMP " +
            "where m.coupleEntityId.value = ?1 and m.memberEntityId.value = ?2")
    void deleteByCoupleIdAndMemberId(Long coupleId, Long memberId);

    @Modifying
    @Transactional
    @Query("update MemberMemoryEntity m set m.memberMemoryState = 'ALIVE', m.deletedAt = NULL " +
            "where m.coupleEntityId.value = ?1")
    void recoverByCoupleId(Long coupleId);
}
