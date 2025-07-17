package makeus.cmc.malmo.application.service;

import lombok.RequiredArgsConstructor;
import makeus.cmc.malmo.application.port.in.GetLoveTypeQuestionsUseCase;
import makeus.cmc.malmo.application.port.out.LoadLoveTypeQuestionDataPort;
import makeus.cmc.malmo.application.port.out.LoadLoveTypeQuestionsPort;
import makeus.cmc.malmo.domain.model.love_type.LoveTypeQuestion;
import makeus.cmc.malmo.domain.model.love_type.LoveTypeQuestionData;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.Map;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class LoveTypeQuestionService implements GetLoveTypeQuestionsUseCase {

    private final LoadLoveTypeQuestionDataPort loadLoveTypeQuestionDataPort;

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
                .build();
    }
}
