package makeus.cmc.malmo.application.port.out.question;

import makeus.cmc.malmo.domain.model.question.MemberAnswer;

public interface SaveMemberAnswerPort {
    MemberAnswer saveMemberAnswer(MemberAnswer memberAnswer);
}
