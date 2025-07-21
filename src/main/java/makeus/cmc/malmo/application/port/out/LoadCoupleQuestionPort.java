package makeus.cmc.malmo.application.port.out;

import makeus.cmc.malmo.domain.model.question.CoupleQuestion;
import makeus.cmc.malmo.domain.service.CoupleQuestionDomainService;
import makeus.cmc.malmo.domain.value.id.CoupleId;
import makeus.cmc.malmo.domain.value.id.CoupleQuestionId;
import makeus.cmc.malmo.domain.value.id.MemberId;

import java.util.Optional;

public interface LoadCoupleQuestionPort {
    Optional<CoupleQuestion> loadMaxLevelCoupleQuestion(CoupleId coupleId);
    Optional<CoupleQuestionDomainService.CoupleQuestionDto> getMaxLevelQuestionDto(MemberId memberId, CoupleId coupleId);
    Optional<CoupleQuestionDomainService.CoupleQuestionDto> getCoupleQuestionDtoByLevel(MemberId memberId, CoupleId coupleId, int level);

    Optional<CoupleQuestion> loadCoupleQuestionById(CoupleQuestionId coupleQuestionId);
}
