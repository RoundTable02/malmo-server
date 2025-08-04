package makeus.cmc.malmo.adaptor.out.persistence.repository.question;

import makeus.cmc.malmo.adaptor.out.persistence.entity.question.QuestionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface QuestionRepository extends JpaRepository<QuestionEntity, Long> {

    Optional<QuestionEntity> findByLevel(int level);
}
