package makeus.cmc.malmo.application.port.out;

import makeus.cmc.malmo.domain.model.question.TempCoupleQuestion;

public interface SaveTempCoupleQuestionPort {
    TempCoupleQuestion saveTempCoupleQuestion(TempCoupleQuestion tempCoupleQuestion);
}
