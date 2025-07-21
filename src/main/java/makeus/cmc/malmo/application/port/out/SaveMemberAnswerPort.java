package makeus.cmc.malmo.application.port.out;

import makeus.cmc.malmo.domain.model.question.MemberAnswer;

public interface SaveMemberAnswerPort {
    MemberAnswer saveMemberAnswer(MemberAnswer memberAnswer);
}
