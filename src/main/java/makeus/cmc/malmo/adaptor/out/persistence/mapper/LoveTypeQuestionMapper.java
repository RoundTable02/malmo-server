package makeus.cmc.malmo.adaptor.out.persistence.mapper;

import makeus.cmc.malmo.adaptor.out.persistence.entity.love_type.LoveTypeQuestionEntity;
import makeus.cmc.malmo.domain.model.love_type.LoveTypeQuestion;
import org.springframework.stereotype.Component;

@Component
public class LoveTypeQuestionMapper {

    public LoveTypeQuestion toDomain(LoveTypeQuestionEntity entity) {
        return LoveTypeQuestion.from(
                entity.getId(),
                entity.getQuestionNumber(),
                entity.isReversed(),
                entity.getContent(),
                entity.getLoveTypeQuestionType(),
                entity.getWeight(),
                entity.getCreatedAt(),
                entity.getModifiedAt(),
                entity.getDeletedAt()
        );
    }
}
