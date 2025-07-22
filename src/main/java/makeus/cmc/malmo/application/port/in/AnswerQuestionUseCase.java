package makeus.cmc.malmo.application.port.in;

import lombok.Builder;
import lombok.Data;

public interface AnswerQuestionUseCase {

    void answerQuestion(AnswerQuestionCommand command);
    void updateAnswer(AnswerQuestionCommand command);

    @Data
    @Builder
    class AnswerQuestionCommand {
        private Long userId;
        private String answer;
    }
}
