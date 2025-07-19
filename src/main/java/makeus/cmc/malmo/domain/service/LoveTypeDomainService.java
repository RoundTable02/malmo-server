package makeus.cmc.malmo.domain.service;

import lombok.RequiredArgsConstructor;
import makeus.cmc.malmo.application.port.out.LoadLoveTypePort;
import makeus.cmc.malmo.application.port.out.LoadLoveTypeQuestionsPort;
import makeus.cmc.malmo.domain.exception.LoveTypeNotFoundException;
import makeus.cmc.malmo.domain.exception.LoveTypeQuestionNotFoundException;
import makeus.cmc.malmo.domain.model.love_type.LoveType;
import makeus.cmc.malmo.domain.model.love_type.LoveTypeQuestion;
import makeus.cmc.malmo.domain.value.id.LoveTypeId;
import makeus.cmc.malmo.domain.value.type.LoveTypeCategory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class LoveTypeDomainService {

    private final LoadLoveTypePort loadLoveTypePort;
    private final LoadLoveTypeQuestionsPort loadLoveTypeQuestionsPort;

    public LoveType getLoveTypeById(LoveTypeId loveTypeId) {
        return loadLoveTypePort.findLoveTypeById(loveTypeId.getValue())
                .orElseThrow(LoveTypeNotFoundException::new);
    }

    public LoveTypeCalculationResult calculateLoveType(List<TestResultInput> results) {
        List<LoveTypeQuestion> loveTypeQuestions = loadLoveTypeQuestionsPort.loadLoveTypeQuestions();
        Map<Long, LoveTypeQuestion> questionMap = loveTypeQuestions.stream()
                .collect(Collectors.toMap(LoveTypeQuestion::getId, q -> q));

        float anxietyScore = 0.0f;
        float avoidanceScore = 0.0f;

        for (TestResultInput result : results) {
            LoveTypeQuestion question = questionMap.get(result.questionId());
            if (question == null) {
                throw new LoveTypeQuestionNotFoundException();
            }
            if (question.isAnxietyType()) {
                anxietyScore += question.getScore(result.score());
            } else {
                avoidanceScore += question.getScore(result.score());
            }
        }

        LoveTypeCategory loveTypeCategory = LoveType.findLoveTypeCategory(avoidanceScore, anxietyScore);
        LoveType finalLoveType = loadLoveTypePort.findLoveTypeByLoveTypeCategory(loveTypeCategory)
                .orElseThrow(LoveTypeNotFoundException::new);

        return new LoveTypeCalculationResult(finalLoveType, avoidanceScore, anxietyScore);
    }

    public record TestResultInput(Long questionId, int score) {}

    public record LoveTypeCalculationResult(LoveType loveType, float avoidanceScore, float anxietyScore) {}

}
