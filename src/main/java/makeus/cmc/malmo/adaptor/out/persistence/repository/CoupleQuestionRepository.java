package makeus.cmc.malmo.adaptor.out.persistence.repository;

import makeus.cmc.malmo.adaptor.out.persistence.entity.question.CoupleQuestionEntity;
import makeus.cmc.malmo.adaptor.out.persistence.repository.custom.CoupleQuestionRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CoupleQuestionRepository extends JpaRepository<CoupleQuestionEntity, Long>, CoupleQuestionRepositoryCustom {
}
