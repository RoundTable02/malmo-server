package makeus.cmc.malmo.domain.service;

import makeus.cmc.malmo.domain.exception.LoveTypeNotFoundException;
import makeus.cmc.malmo.domain.model.love_type.LoveTypeQuestionData;
import makeus.cmc.malmo.domain.value.type.LoveTypeCategory;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

@Component
public class LoveTypeCalculator {

    public LoveTypeCalculationResult calculate(List<TestResultInput> results, Function<Long, LoveTypeQuestionData> questionProvider) {
        float anxietyScore = 0.0f;
        float avoidanceScore = 0.0f;

        for (TestResultInput result : results) {
            LoveTypeQuestionData question = questionProvider.apply(result.questionId());

            if (question.isAnxietyType()) {
                anxietyScore += question.getScore(result.score());
            } else {
                avoidanceScore += question.getScore(result.score());
            }
        }

        anxietyScore = formatScore(anxietyScore);
        avoidanceScore = formatScore(avoidanceScore);

        LoveTypeCategory category = matchLoveType(anxietyScore, avoidanceScore);

        return new LoveTypeCalculationResult(category, avoidanceScore, anxietyScore);
    }

    private float formatScore(float raw) {
        return (float) Math.floor((raw / 18.0f * 100.0f)) / 100.0f;
    }

    private LoveTypeCategory matchLoveType(float anxiety, float avoidance) {
        return Arrays.stream(LoveTypeCategory.values())
                .filter(c ->
                        anxiety >= c.getAnxietyOver()
                                && anxiety < c.getAnxietyUnder()
                                && avoidance >= c.getAvoidanceOver()
                                && avoidance < c.getAvoidanceUnder())
                .findFirst()
                .orElseThrow(LoveTypeNotFoundException::new);
    }

    public record TestResultInput(Long questionId, int score) {}
    public record LoveTypeCalculationResult(LoveTypeCategory category, float avoidanceScore, float anxietyScore) {}
}
