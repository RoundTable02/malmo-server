package makeus.cmc.malmo.application.service;

import lombok.RequiredArgsConstructor;
import makeus.cmc.malmo.application.port.in.GetLoveTypeQuestionsUseCase;
import makeus.cmc.malmo.application.port.out.LoadLoveTypeQuestionsPort;
import makeus.cmc.malmo.domain.model.love_type.LoveTypeQuestion;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class LoveTypeQuestionService implements GetLoveTypeQuestionsUseCase {

    private final LoadLoveTypeQuestionsPort loadLoveTypeQuestionsPort;

    @Override
    public LoveTypeQuestionsResponseDto getLoveTypeQuestions() {
        List<LoveTypeQuestion> loveTypeQuestions = loadLoveTypeQuestionsPort.loadLoveTypeQuestions();

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
