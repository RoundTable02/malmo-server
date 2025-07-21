package makeus.cmc.malmo.adaptor.out.persistence.mapper;

import makeus.cmc.malmo.adaptor.out.persistence.entity.question.QuestionEntity;
import makeus.cmc.malmo.domain.model.question.Question;
import org.springframework.stereotype.Component;

@Component
public class QuestionMapper {

    public Question toDomain(QuestionEntity entity) {
        if (entity == null) {
            return null;
        }
        return Question.from(
                entity.getId(),
                entity.getTitle(),
                entity.getContent(),
                entity.getLevel(),
                entity.getCreatedAt(),
                entity.getModifiedAt(),
                entity.getDeletedAt()
        );
    }

    public QuestionEntity toEntity(Question question) {
        if (question == null) {
            return null;
        }
        return QuestionEntity.builder()
                .id(question.getId())
                .title(question.getTitle())
                .content(question.getContent())
                .level(question.getLevel())
                .createdAt(question.getCreatedAt())
                .modifiedAt(question.getModifiedAt())
                .deletedAt(question.getDeletedAt())
                .build();
    }
}
