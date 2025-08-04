package makeus.cmc.malmo.application.port.in.question;

import lombok.Builder;
import lombok.Data;

public interface AnswerQuestionUseCase {

    QuestionAnswerResponse answerQuestion(AnswerQuestionCommand command);
    QuestionAnswerResponse updateAnswer(AnswerQuestionCommand command);

    @Data
    @Builder
    class AnswerQuestionCommand {
        private Long userId;
        private String answer;
    }

    @Data
    @Builder
    class QuestionAnswerResponse {
        private Long coupleQuestionId;
    }
}
