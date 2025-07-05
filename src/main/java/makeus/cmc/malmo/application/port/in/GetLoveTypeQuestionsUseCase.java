package makeus.cmc.malmo.application.port.in;

import lombok.Builder;
import lombok.Data;

import java.util.List;

public interface GetLoveTypeQuestionsUseCase {

    LoveTypeQuestionsResponseDto getLoveTypeQuestions();

    @Data
    @Builder
    class LoveTypeQuestionsResponseDto {
        private List<LoveTypeQuestionDto> list;
    }

    @Data
    @Builder
    class LoveTypeQuestionDto {
        private int questionNumber;
        private String content;
    }
}
