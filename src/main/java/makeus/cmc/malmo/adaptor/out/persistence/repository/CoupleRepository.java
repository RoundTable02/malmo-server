package makeus.cmc.malmo.adaptor.out.persistence.repository;

import makeus.cmc.malmo.adaptor.out.persistence.entity.couple.CoupleEntity;
import makeus.cmc.malmo.adaptor.out.persistence.repository.custom.CoupleRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface CoupleRepository extends JpaRepository<CoupleEntity, Long>, CoupleRepositoryCustom {

    @Query("SELECT cm.id FROM CoupleMemberEntity cm WHERE cm.memberEntityId.value = :memberId AND cm.coupleMemberState = 'ALIVE'")
    Long findCoupleMemberIdByMemberId(Long memberId);

    @Query("SELECT cm.coupleEntityId.value FROM CoupleMemberEntity cm WHERE cm.memberEntityId.value = :memberId AND cm.coupleMemberState = 'ALIVE'")
    Long findCoupleIdByMemberId(Long memberId);
}
