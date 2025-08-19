package makeus.cmc.malmo.adaptor.out.persistence.mapper;

import makeus.cmc.malmo.adaptor.out.persistence.entity.TempLoveTypeEntity;
import makeus.cmc.malmo.domain.model.love_type.TempLoveType;
import org.springframework.stereotype.Component;

@Component
public class TempLoveTypeMapper {

    public TempLoveType toDomain(TempLoveTypeEntity entity) {
        if (entity == null) {
            return null;
        }
        return TempLoveType.from(
                entity.getId(),
                entity.getCategory(),
                entity.getAvoidanceRate(),
                entity.getAnxietyRate(),
                entity.getCreatedAt(),
                entity.getModifiedAt(),
                entity.getDeletedAt()
        );
    }

    public TempLoveTypeEntity toEntity(TempLoveType tempLoveType) {
        if (tempLoveType == null) {
            return null;
        }
        return TempLoveTypeEntity.builder()
                .id(tempLoveType.getId())
                .category(tempLoveType.getCategory())
                .avoidanceRate(tempLoveType.getAvoidanceRate())
                .anxietyRate(tempLoveType.getAnxietyRate())
                .createdAt(tempLoveType.getCreatedAt())
                .modifiedAt(tempLoveType.getModifiedAt())
                .deletedAt(tempLoveType.getDeletedAt())
                .build();
    }
}
