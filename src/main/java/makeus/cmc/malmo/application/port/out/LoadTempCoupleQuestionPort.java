package makeus.cmc.malmo.application.port.out;

import makeus.cmc.malmo.domain.model.question.TempCoupleQuestion;
import makeus.cmc.malmo.domain.value.id.MemberId;

import java.util.Optional;

public interface LoadTempCoupleQuestionPort {
    Optional<TempCoupleQuestion> loadTempCoupleQuestionByMemberId(MemberId memberId);
}
