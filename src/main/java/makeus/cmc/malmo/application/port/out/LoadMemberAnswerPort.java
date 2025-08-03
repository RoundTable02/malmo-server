package makeus.cmc.malmo.application.port.out;

import makeus.cmc.malmo.application.service.helper.question.CoupleQuestionQueryHelper;
import makeus.cmc.malmo.domain.model.question.MemberAnswer;
import makeus.cmc.malmo.domain.value.id.CoupleQuestionId;
import makeus.cmc.malmo.domain.value.id.MemberId;

import java.util.Optional;

public interface LoadMemberAnswerPort {
    Optional<CoupleQuestionQueryHelper.MemberAnswersDto> getQuestionAnswers(MemberId memberId, CoupleQuestionId coupleQuestionId);
    Optional<MemberAnswer> getMemberAnswer(CoupleQuestionId coupleQuestionId, MemberId memberId);

    boolean isMemberAnswered(CoupleQuestionId coupleQuestionId, MemberId memberId);

    long countAnswers(CoupleQuestionId coupleQuestionId);
}
