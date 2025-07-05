package makeus.cmc.malmo.adaptor.out.persistence.repository;

import makeus.cmc.malmo.adaptor.out.persistence.entity.love_type.LoveTypeQuestionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LoveTypeQuestionRepository extends JpaRepository<LoveTypeQuestionEntity, Long> {

    List<LoveTypeQuestionEntity> findAllByOrderByQuestionNumberAsc();
}
