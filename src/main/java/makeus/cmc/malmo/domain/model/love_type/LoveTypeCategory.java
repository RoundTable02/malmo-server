package makeus.cmc.malmo.domain.model.love_type;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum LoveTypeCategory {
    STABLE_TYPE(LoveTypeCategory.MIN_SCORE, 2.33f, LoveTypeCategory.MIN_SCORE, 2.61f),
    ANXIETY_TYPE(LoveTypeCategory.MIN_SCORE, 2.33f, 2.61f, LoveTypeCategory.MAX_SCORE),
    AVOIDANCE_TYPE(2.33f, LoveTypeCategory.MAX_SCORE, LoveTypeCategory.MIN_SCORE, 2.61f),
    CONFUSION_TYPE(2.33f, LoveTypeCategory.MAX_SCORE, 2.61f, LoveTypeCategory.MAX_SCORE);

    private final float avoidanceOver;
    private final float avoidanceUnder;
    private final float anxietyOver;
    private final float anxietyUnder;

    public static final float MAX_SCORE = 5.0f;
    public static final float MIN_SCORE = 0.0f;
}
