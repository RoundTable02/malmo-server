package makeus.cmc.malmo.adaptor.out.persistence.mapper;

import makeus.cmc.malmo.adaptor.out.persistence.entity.love_type.LoveTypeCategoryJpa;
import makeus.cmc.malmo.adaptor.out.persistence.entity.love_type.LoveTypeEntity;
import makeus.cmc.malmo.domain.model.love_type.LoveType;
import makeus.cmc.malmo.domain.model.love_type.LoveTypeCategory;
import org.springframework.stereotype.Component;

@Component
public class LoveTypeMapper {

    public LoveType toDomain(LoveTypeEntity entity) {
        if (entity == null) {
            return null;
        }

        return LoveType.builder()
                .id(entity.getId())
                .title(entity.getTitle())
                .summary(entity.getSummary())
                .content(entity.getContent())
                .imageUrl(entity.getImageUrl())
                .loveTypeCategory(toLoveTypeCategory(entity.getLoveTypeCategory()))
                .createdAt(entity.getCreatedAt())
                .modifiedAt(entity.getModifiedAt())
                .deletedAt(entity.getDeletedAt())
                .build();
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
                .loveTypeCategory(toLoveTypeCategoryJpa(domain.getLoveTypeCategory()))
                .createdAt(domain.getCreatedAt())
                .modifiedAt(domain.getModifiedAt())
                .deletedAt(domain.getDeletedAt())
                .build();
    }

    private LoveTypeCategory toLoveTypeCategory(LoveTypeCategoryJpa loveTypeCategoryJpa) {
        return loveTypeCategoryJpa == null ? null : LoveTypeCategory.valueOf(loveTypeCategoryJpa.name());
    }

    private LoveTypeCategoryJpa toLoveTypeCategoryJpa(LoveTypeCategory loveTypeCategory) {
        return loveTypeCategory == null ? null : LoveTypeCategoryJpa.valueOf(loveTypeCategory.name());
    }
}