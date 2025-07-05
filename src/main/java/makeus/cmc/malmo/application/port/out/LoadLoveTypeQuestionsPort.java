package makeus.cmc.malmo.application.port.out;

import makeus.cmc.malmo.domain.model.love_type.LoveTypeQuestion;

import java.util.List;

public interface LoadLoveTypeQuestionsPort {

    List<LoveTypeQuestion> loadLoveTypeQuestions();
}
