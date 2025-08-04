package makeus.cmc.malmo.application.helper.question;

import lombok.RequiredArgsConstructor;
import makeus.cmc.malmo.application.port.out.question.SaveCoupleQuestionPort;
import makeus.cmc.malmo.application.port.out.question.SaveMemberAnswerPort;
import makeus.cmc.malmo.application.port.out.question.SaveTempCoupleQuestionPort;
import makeus.cmc.malmo.domain.model.question.CoupleQuestion;
import makeus.cmc.malmo.domain.model.question.MemberAnswer;
import makeus.cmc.malmo.domain.model.question.TempCoupleQuestion;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CoupleQuestionCommandHelper {

    private final SaveCoupleQuestionPort saveCoupleQuestionPort;
    private final SaveTempCoupleQuestionPort saveTempCoupleQuestionPort;
    private final SaveMemberAnswerPort saveMemberAnswerPort;

    public TempCoupleQuestion saveTempCoupleQuestion(TempCoupleQuestion tempCoupleQuestion) {
        return saveTempCoupleQuestionPort.saveTempCoupleQuestion(tempCoupleQuestion);
    }

    public CoupleQuestion saveCoupleQuestion(CoupleQuestion coupleQuestion) {
        return saveCoupleQuestionPort.saveCoupleQuestion(coupleQuestion);
    }

    public MemberAnswer saveMemberAnswer(MemberAnswer memberAnswer) {
        return saveMemberAnswerPort.saveMemberAnswer(memberAnswer);
    }
}
