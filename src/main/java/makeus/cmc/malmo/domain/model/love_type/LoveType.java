package makeus.cmc.malmo.domain.model.love_type;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.SuperBuilder;
import makeus.cmc.malmo.domain.model.BaseTimeEntity;
import makeus.cmc.malmo.domain.value.type.LoveTypeCategory;

@Getter
@SuperBuilder
@AllArgsConstructor
public class LoveType extends BaseTimeEntity {
    private Long id;
    private String title;
    private String summary;
    private String content;
    private String imageUrl;
    private LoveTypeCategory loveTypeCategory;

    public static LoveTypeCategory findLoveTypeCategory(float avoidanceRate, float anxietyRate) {
        float avoidanceRateResult = avoidanceRate / 18.0f;
        float anxietyRateResult = anxietyRate / 18.0f;

        if (avoidanceRateResult < 2.33 && anxietyRateResult < 2.61) {
            return LoveTypeCategory.STABLE_TYPE;
        } else if (avoidanceRateResult >= 2.33 && anxietyRateResult < 2.61) {
            return LoveTypeCategory.AVOIDANCE_TYPE;
        } else if (avoidanceRateResult < 2.33 && anxietyRateResult >= 2.61) {
            return LoveTypeCategory.ANXIETY_TYPE;
        } else {
            return LoveTypeCategory.CONFUSION_TYPE;
        }
    }
}