package makeus.cmc.malmo.domain.service;

import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Component
public class CoupleQuestionDomainService {

    public static final int FIRST_QUESTION_LEVEL = 1;

    public boolean needsNextQuestion(LocalDateTime bothAnsweredAt) {
        if (bothAnsweredAt == null) {
            return false;
        }

        return bothAnsweredAt.toLocalDate().isBefore(LocalDate.now());
    }
}
