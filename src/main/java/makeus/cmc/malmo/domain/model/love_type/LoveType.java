package makeus.cmc.malmo.domain.model.love_type;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.SuperBuilder;
import makeus.cmc.malmo.domain.model.BaseTimeEntity;

@Getter
@SuperBuilder
@AllArgsConstructor
public class LoveType extends BaseTimeEntity {
    private Long id;
    private String title;
    private String content;
    private String imageUrl;
    private float weight;
    private LoveTypeCategory loveTypeCategory;

    public static LoveTypeCategory findLoveTypeCategory(float avoidanceRate, float anxietyRate) {
        if (avoidanceRate < 2.33 && anxietyRate < 2.61) {
            return LoveTypeCategory.STABLE_TYPE;
        } else if (avoidanceRate >= 2.33 && anxietyRate < 2.61) {
            return LoveTypeCategory.AVOIDANCE_TYPE;
        } else if (avoidanceRate < 2.33 && anxietyRate >= 2.61) {
            return LoveTypeCategory.ANXIETY_TYPE;
        } else {
            return LoveTypeCategory.CONFUSION_TYPE;
        }
    }
}