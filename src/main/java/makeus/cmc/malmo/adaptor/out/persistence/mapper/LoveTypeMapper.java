package makeus.cmc.malmo.adaptor.out.persistence.mapper;

import makeus.cmc.malmo.adaptor.out.persistence.entity.love_type.LoveTypeEntity;
import makeus.cmc.malmo.domain.model.love_type.LoveType;
import org.springframework.stereotype.Component;

@Component
public class LoveTypeMapper {

    public LoveType toDomain(LoveTypeEntity entity) {
        return LoveType.from(
                entity.getId(),
                entity.getTitle(),
                entity.getSummary(),
                entity.getContent(),
                entity.getImageUrl(),
                entity.getLoveTypeCategory(),
                entity.getCreatedAt(),
                entity.getModifiedAt(),
                entity.getDeletedAt()
        );
    }

    public LoveTypeEntity toEntity(LoveType domain) {
        if (domain == null) {
            return null;
        }

        return LoveTypeEntity.builder()
                .id(domain.getId())
                .title(domain.getTitle())
                .summary(domain.getSummary())
                .content(domain.getContent())
                .imageUrl(domain.getImageUrl())
                .loveTypeCategory(domain.getLoveTypeCategory())
                .createdAt(domain.getCreatedAt())
                .modifiedAt(domain.getModifiedAt())
                .deletedAt(domain.getDeletedAt())
                .build();
    }
}