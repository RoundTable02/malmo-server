package makeus.cmc.malmo.application.port.out;

import makeus.cmc.malmo.domain.model.question.CoupleQuestion;
import makeus.cmc.malmo.domain.service.CoupleQuestionDomainService;
import makeus.cmc.malmo.domain.value.id.CoupleId;
import makeus.cmc.malmo.domain.value.id.CoupleQuestionId;

import java.util.Optional;

public interface LoadCoupleQuestionPort {
    Optional<CoupleQuestion> loadMaxLevelCoupleQuestion(CoupleId coupleId);
    Optional<CoupleQuestionDomainService.CoupleQuestionDto> getMaxLevelQuestionDto(CoupleId coupleId);
    Optional<CoupleQuestionDomainService.CoupleQuestionDto> getCoupleQuestionDtoByLevel(CoupleId coupleId, int level);

    Optional<CoupleQuestion> loadCoupleQuestionById(CoupleQuestionId coupleQuestionId);
}
