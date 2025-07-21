package makeus.cmc.malmo.application.port.out;

import makeus.cmc.malmo.domain.model.question.MemberAnswer;
import makeus.cmc.malmo.domain.service.CoupleQuestionDomainService;
import makeus.cmc.malmo.domain.value.id.CoupleQuestionId;
import makeus.cmc.malmo.domain.value.id.MemberId;

import java.util.Optional;

public interface LoadMemberAnswerPort {
    Optional<CoupleQuestionDomainService.MemberAnswersDto> getQuestionAnswers(MemberId memberId, CoupleQuestionId coupleQuestionId);
    Optional<MemberAnswer> getMemberAnswer(CoupleQuestionId coupleQuestionId, MemberId memberId);

    boolean isMemberAnswered(CoupleQuestionId coupleQuestionId, MemberId memberId);

    long countAnswers(CoupleQuestionId coupleQuestionId);
}
