package makeus.cmc.malmo.integration_test.dto_factory;

import makeus.cmc.malmo.adaptor.in.web.controller.QuestionController;

public class CoupleQuestionRequestDtoFactory {
    public static QuestionController.AnswerRequestDto createAnswerRequestDto(String answer) {
        QuestionController.AnswerRequestDto dto = new QuestionController.AnswerRequestDto();
        dto.setAnswer(answer);
        return dto;
    }
}
