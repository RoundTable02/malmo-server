package makeus.cmc.malmo.domain.model.love_type;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import makeus.cmc.malmo.domain.value.type.LoveTypeCategory;

import java.time.LocalDateTime;

@Getter
@Builder(access = AccessLevel.PRIVATE)
public class TempLoveType {
    private Long id;
    private LoveTypeCategory category;
    private float avoidanceRate;
    private float anxietyRate;

    // BaseTimeEntity fields
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;
    private LocalDateTime deletedAt;

    public static TempLoveType createTempLoveType(LoveTypeCategory category, float avoidanceRate, float anxietyRate) {
        return TempLoveType.builder()
                .category(category)
                .avoidanceRate(avoidanceRate)
                .anxietyRate(anxietyRate)
                .build();
    }

    public static TempLoveType from(Long id, LoveTypeCategory category, float avoidanceRate, float anxietyRate,
                                    LocalDateTime createdAt, LocalDateTime modifiedAt, LocalDateTime deletedAt) {
        return TempLoveType.builder()
                .id(id)
                .category(category)
                .avoidanceRate(avoidanceRate)
                .anxietyRate(anxietyRate)
                .createdAt(createdAt)
                .modifiedAt(modifiedAt)
                .deletedAt(deletedAt)
                .build();
    }
}
