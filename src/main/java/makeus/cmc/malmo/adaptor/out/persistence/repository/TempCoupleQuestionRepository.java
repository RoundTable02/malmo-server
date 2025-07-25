package makeus.cmc.malmo.adaptor.out.persistence.repository;

import makeus.cmc.malmo.adaptor.out.persistence.entity.question.TempCoupleQuestionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface TempCoupleQuestionRepository extends JpaRepository<TempCoupleQuestionEntity, Long> {

    @Query("select t from TempCoupleQuestionEntity t where t.memberId.value = ?1 and t.coupleQuestionState = 'ALIVE'")
    Optional<TempCoupleQuestionEntity> findByMemberId_Value(Long memberId);
}
