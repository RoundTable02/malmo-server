package makeus.cmc.malmo.domain.service;

import lombok.*;
import makeus.cmc.malmo.application.port.out.*;
import makeus.cmc.malmo.application.service.helper.question.CoupleQuestionQueryHelper;
import makeus.cmc.malmo.domain.exception.CoupleQuestionNotFoundException;
import makeus.cmc.malmo.domain.exception.MemberAccessDeniedException;
import makeus.cmc.malmo.domain.exception.QuestionNotFoundException;
import makeus.cmc.malmo.domain.model.question.CoupleQuestion;
import makeus.cmc.malmo.domain.model.question.MemberAnswer;
import makeus.cmc.malmo.domain.model.question.Question;
import makeus.cmc.malmo.domain.model.question.TempCoupleQuestion;
import makeus.cmc.malmo.domain.value.id.CoupleId;
import makeus.cmc.malmo.domain.value.id.CoupleMemberId;
import makeus.cmc.malmo.domain.value.id.CoupleQuestionId;
import makeus.cmc.malmo.domain.value.id.MemberId;
import makeus.cmc.malmo.domain.value.state.CoupleQuestionState;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;

@RequiredArgsConstructor
@Service
public class CoupleQuestionDomainService {

    public static final int FIRST_QUESTION_LEVEL = 1;

    public boolean needsNextQuestion(LocalDateTime bothAnsweredAt) {
        if (bothAnsweredAt == null) {
            return false;
        }

        return bothAnsweredAt.toLocalDate().isBefore(LocalDate.now());
    }
}
