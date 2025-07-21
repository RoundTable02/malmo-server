package makeus.cmc.malmo.application.port.out;

import makeus.cmc.malmo.domain.model.question.CoupleQuestion;
import makeus.cmc.malmo.domain.model.question.MemberAnswer;

public interface SaveCoupleQuestionPort {
    CoupleQuestion saveCoupleQuestion(CoupleQuestion coupleQuestion);

    void saveMemberAnswer(MemberAnswer memberAnswer);
}
