package makeus.cmc.malmo.application.port.out.question;

import makeus.cmc.malmo.domain.model.question.CoupleQuestion;

public interface SaveCoupleQuestionPort {
    CoupleQuestion saveCoupleQuestion(CoupleQuestion coupleQuestion);
}
