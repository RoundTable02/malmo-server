package makeus.cmc.malmo.adaptor.out.persistence.mapper;

import makeus.cmc.malmo.adaptor.out.persistence.entity.love_type.LoveTypeEntity;
import makeus.cmc.malmo.domain.model.love_type.LoveType;
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
                .content(entity.getContent())
                .imageUrl(entity.getImageUrl())
                .weight(entity.getWeight())
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
                .content(domain.getContent())
                .imageUrl(domain.getImageUrl())
                .weight(domain.getWeight())
                .build();
    }
}