package makeus.cmc.malmo.application.service;

import lombok.RequiredArgsConstructor;
import makeus.cmc.malmo.application.helper.love_type.LoveTypeQueryHelper;
import makeus.cmc.malmo.application.helper.love_type.TempLoveTypeHelper;
import makeus.cmc.malmo.application.port.in.CalculateQuestionResultUseCase;
import makeus.cmc.malmo.application.port.in.GetLoveTypeQuestionResultUseCase;
import makeus.cmc.malmo.application.port.in.GetLoveTypeQuestionsUseCase;
import makeus.cmc.malmo.application.port.out.LoadLoveTypeQuestionDataPort;
import makeus.cmc.malmo.domain.model.love_type.LoveTypeQuestionData;
import makeus.cmc.malmo.domain.model.love_type.TempLoveType;
import makeus.cmc.malmo.domain.service.LoveTypeCalculator;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LoveTypeQuestionService
        implements GetLoveTypeQuestionsUseCase, CalculateQuestionResultUseCase, GetLoveTypeQuestionResultUseCase {

    private final LoadLoveTypeQuestionDataPort loadLoveTypeQuestionDataPort;
    private final LoveTypeCalculator loveTypeCalculator;
    private final LoveTypeQueryHelper loveTypeQueryHelper;

    private final TempLoveTypeHelper tempLoveTypeHelper;


    @Override
    public LoveTypeQuestionsResponseDto getLoveTypeQuestions() {
        Map<Long, LoveTypeQuestionData> map = loadLoveTypeQuestionDataPort.loadLoveTypeData();
        List<LoveTypeQuestionData> loveTypeQuestions = map.values().stream()
                        .sorted(Comparator.comparingInt(LoveTypeQuestionData::getQuestionNumber))
                        .toList();

        List<LoveTypeQuestionDto> loveTypeQuestionDtoList = loveTypeQuestions.stream()
                .map(l -> LoveTypeQuestionDto.builder()
                        .questionNumber(l.getQuestionNumber())
                        .content(l.getContent())
                        .build())
                .toList();

        return LoveTypeQuestionsResponseDto.builder()
                .list(loveTypeQuestionDtoList)
                .totalCount((long) loveTypeQuestionDtoList.size())
                .build();
    }

    @Override
    public CalculateResultResponse calculateResult(UpdateMemberLoveTypeCommand command) {

        List<LoveTypeCalculator.TestResultInput> testResultInputs = command.getResults().stream()
                .map(result -> new LoveTypeCalculator.TestResultInput(result.getQuestionId(), result.getScore()))
                .collect(Collectors.toList());

        LoveTypeCalculator.LoveTypeCalculationResult calculationResult =
                loveTypeCalculator.calculate(testResultInputs, loveTypeQueryHelper::getQuestionById);

        TempLoveType tempLoveType = TempLoveType.createTempLoveType(
                calculationResult.category(),
                calculationResult.avoidanceScore(),
                calculationResult.anxietyScore()
        );

        TempLoveType savedTempLoveType = tempLoveTypeHelper.saveTempLoveType(tempLoveType);

        return CalculateResultResponse.builder()
                .loveTypeId(savedTempLoveType.getId())
                .loveTypeCategory(savedTempLoveType.getCategory())
                .avoidanceRate(savedTempLoveType.getAvoidanceRate())
                .anxietyRate(savedTempLoveType.getAnxietyRate())
                .build();
    }

    @Override
    public LoveTypeResultResponse getResult(GetLoveTypeResultCommand command) {
        TempLoveType tempLoveType = tempLoveTypeHelper.getTempLoveTypeByIdOrThrow(command.getLoveTypeId());
        return LoveTypeResultResponse.builder()
                .loveTypeId(tempLoveType.getId())
                .loveTypeCategory(tempLoveType.getCategory())
                .avoidanceRate(tempLoveType.getAvoidanceRate())
                .anxietyRate(tempLoveType.getAnxietyRate())
                .build();
    }
}
