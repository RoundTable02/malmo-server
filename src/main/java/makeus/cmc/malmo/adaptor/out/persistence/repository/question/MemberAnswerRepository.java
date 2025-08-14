package makeus.cmc.malmo.adaptor.out.persistence.repository.question;

import makeus.cmc.malmo.adaptor.out.persistence.entity.question.MemberAnswerEntity;
import makeus.cmc.malmo.domain.model.question.MemberAnswer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface MemberAnswerRepository extends JpaRepository<MemberAnswerEntity, Long>, MemberAnswerRepositoryCustom {
    @Query("SELECT ma FROM MemberAnswerEntity ma " +
           "WHERE ma.coupleQuestionEntityId.value = :coupleQuestionId AND ma.coupleMemberEntityId.value = :coupleMemberId")
    Optional<MemberAnswerEntity> findByCoupleQuestionIdAndCoupleMemberId(Long coupleQuestionId, Long coupleMemberId);
}
