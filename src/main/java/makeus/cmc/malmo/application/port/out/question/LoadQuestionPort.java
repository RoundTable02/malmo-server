package makeus.cmc.malmo.application.port.out.question;

import makeus.cmc.malmo.domain.model.question.Question;

import java.util.Optional;

public interface LoadQuestionPort {
    Optional<Question> loadQuestionByLevel(int level);
}
