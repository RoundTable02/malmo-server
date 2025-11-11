package makeus.cmc.malmo.adaptor.out.persistence.mapper;

import makeus.cmc.malmo.adaptor.out.persistence.entity.chat.DetailedPromptEntity;
import makeus.cmc.malmo.domain.model.chat.DetailedPrompt;
import org.springframework.stereotype.Component;

@Component
public class DetailedPromptMapper {

    public DetailedPrompt toDomain(DetailedPromptEntity entity) {
        return DetailedPrompt.from(
                entity.getId(),
                entity.getLevel(),
                entity.getDetailedLevel(),
                entity.getContent(),
                entity.isForValidation(),
                entity.isForSummary(),
                entity.getMetadataTitle(),
                entity.isLastDetailedPrompt(),
                entity.isForGuideline(),
                entity.getCreatedAt(),
                entity.getModifiedAt(),
                entity.getDeletedAt()
        );
    }

    public DetailedPromptEntity toEntity(DetailedPrompt domain) {
        return DetailedPromptEntity.builder()
                .id(domain.getId())
                .level(domain.getLevel())
                .detailedLevel(domain.getDetailedLevel())
                .content(domain.getContent())
                .isForValidation(domain.isForValidation())
                .isForSummary(domain.isForSummary())
                .metadataTitle(domain.getMetadataTitle())
                .isLastDetailedPrompt(domain.isLastDetailedPrompt())
                .isForGuideline(domain.isForGuideline())
                .createdAt(domain.getCreatedAt())
                .modifiedAt(domain.getModifiedAt())
                .deletedAt(domain.getDeletedAt())
                .build();
    }
}
