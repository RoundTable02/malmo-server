package makeus.cmc.malmo.adaptor.out.persistence.mapper;

import makeus.cmc.malmo.adaptor.out.persistence.entity.question.QuestionEntity;
import makeus.cmc.malmo.domain.model.question.Question;
import org.springframework.stereotype.Component;

@Component
public class QuestionMapper {

    public Question toDomain(QuestionEntity entity) {
        return Question.builder()
                .id(entity.getId())
                .title(entity.getTitle())
                .content(entity.getContent())
                .createdAt(entity.getCreatedAt())
                .modifiedAt(entity.getModifiedAt())
                .deletedAt(entity.getDeletedAt())
                .build();
    }

    public QuestionEntity toEntity(Question domain) {
        return QuestionEntity.builder()
                .id(domain.getId())
                .title(domain.getTitle())
                .content(domain.getContent())
                .build();
    }
}