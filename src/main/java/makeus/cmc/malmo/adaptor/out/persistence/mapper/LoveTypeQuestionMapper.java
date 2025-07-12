package makeus.cmc.malmo.adaptor.out.persistence.mapper;

import makeus.cmc.malmo.adaptor.out.persistence.entity.love_type.LoveTypeQuestionEntity;
import makeus.cmc.malmo.domain.model.love_type.LoveTypeQuestion;
import org.springframework.stereotype.Component;

@Component
public class LoveTypeQuestionMapper {

    public LoveTypeQuestion toDomain(LoveTypeQuestionEntity entity) {
        if (entity == null) {
            return null;
        }

        return LoveTypeQuestion.builder()
                .id(entity.getId())
                .questionNumber(entity.getQuestionNumber())
                .isReversed(entity.isReversed())
                .content(entity.getContent())
                .loveTypeQuestionType(entity.getLoveTypeQuestionType())
                .weight(entity.getWeight())
                .createdAt(entity.getCreatedAt())
                .modifiedAt(entity.getModifiedAt())
                .deletedAt(entity.getDeletedAt())
                .build();
    }
}
