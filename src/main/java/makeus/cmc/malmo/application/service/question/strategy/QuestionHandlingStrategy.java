package makeus.cmc.malmo.application.service.question.strategy;

import makeus.cmc.malmo.application.port.in.question.AnswerQuestionUseCase;
import makeus.cmc.malmo.application.port.in.question.GetQuestionAnswerUseCase;
import makeus.cmc.malmo.application.port.in.question.GetQuestionUseCase;

public interface QuestionHandlingStrategy {

    GetQuestionUseCase.GetQuestionResponse getTodayQuestion(GetQuestionUseCase.GetTodayQuestionCommand command);

    GetQuestionAnswerUseCase.AnswerResponseDto getQuestionAnswers(GetQuestionAnswerUseCase.GetQuestionAnswerCommand command);

    AnswerQuestionUseCase.QuestionAnswerResponse answerQuestion(AnswerQuestionUseCase.AnswerQuestionCommand command);

    AnswerQuestionUseCase.QuestionAnswerResponse updateAnswer(AnswerQuestionUseCase.AnswerQuestionCommand command);
}
