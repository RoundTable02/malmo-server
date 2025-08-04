package makeus.cmc.malmo.adaptor.out.persistence.repository.question;

import makeus.cmc.malmo.adaptor.out.persistence.entity.question.CoupleQuestionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CoupleQuestionRepository extends JpaRepository<CoupleQuestionEntity, Long>, CoupleQuestionRepositoryCustom {
}
