package makeus.cmc.malmo.application.port.out;

import makeus.cmc.malmo.domain.model.question.CoupleQuestion;

public interface SaveCoupleQuestionPort {
    CoupleQuestion saveCoupleQuestion(CoupleQuestion coupleQuestion);
}
