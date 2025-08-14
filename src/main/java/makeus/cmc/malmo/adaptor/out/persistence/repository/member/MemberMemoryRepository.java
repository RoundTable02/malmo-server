package makeus.cmc.malmo.adaptor.out.persistence.repository.member;

import makeus.cmc.malmo.adaptor.out.persistence.entity.member.MemberMemoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface MemberMemoryRepository extends JpaRepository<MemberMemoryEntity, Long> {

    @Query("select m from MemberMemoryEntity m where m.memberEntityId.value = ?1 and m.memberMemoryState = 'ALIVE'")
    List<MemberMemoryEntity> findByMemberEntityId_Value(Long memberId);

    @Modifying
    @Query("UPDATE MemberMemoryEntity m SET m.memberMemoryState = 'DELETED', m.deletedAt = CURRENT_TIMESTAMP WHERE m.memberMemoryState = 'ALIVE' AND m.memberEntityId.value = :memberId")
    void updateMemberMemoryStateToDeleted(Long memberId);
}
