package makeus.cmc.malmo.application.service.strategy;

import makeus.cmc.malmo.application.port.in.AnswerQuestionUseCase;
import makeus.cmc.malmo.application.port.in.GetQuestionAnswerUseCase;
import makeus.cmc.malmo.application.port.in.GetQuestionUseCase;

public interface QuestionHandlingStrategy {

    GetQuestionUseCase.GetQuestionResponse getTodayQuestion(GetQuestionUseCase.GetTodayQuestionCommand command);

    GetQuestionAnswerUseCase.AnswerResponseDto getQuestionAnswers(GetQuestionAnswerUseCase.GetQuestionAnswerCommand command);

    AnswerQuestionUseCase.QuestionAnswerResponse answerQuestion(AnswerQuestionUseCase.AnswerQuestionCommand command);

    AnswerQuestionUseCase.QuestionAnswerResponse updateAnswer(AnswerQuestionUseCase.AnswerQuestionCommand command);
}
