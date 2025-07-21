package makeus.cmc.malmo.domain.service;

import lombok.Data;
import lombok.Getter;
import makeus.cmc.malmo.domain.model.question.CoupleQuestion;
import makeus.cmc.malmo.domain.model.question.Question;
import makeus.cmc.malmo.domain.model.question.TempCoupleQuestion;
import makeus.cmc.malmo.domain.value.id.CoupleId;
import makeus.cmc.malmo.domain.value.id.MemberId;
import makeus.cmc.malmo.domain.value.state.CoupleQuestionState;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
public class CoupleQuestionDomainService {

    public QuestionRepositoryDto getMaxLevelQuestion(CoupleId coupleId) {
        return null;
    }

    public TempCoupleQuestion getTempCoupleQuestion(MemberId memberId) {
        return null;
    }

    @Transactional
    public CoupleQuestion createNextCoupleQuestion(CoupleId coupleId, int level) {
        return null;
    }

    @Data
    public class QuestionRepositoryDto {
        private Long id;
        private String title;
        private String content;
        private int level;
        private CoupleId coupleId;
        private CoupleQuestionState coupleQuestionState;
        private LocalDateTime bothAnsweredAt;
        private boolean meAnswered;
        private boolean partnerAnswered;
        private LocalDateTime createdAt;
    }


    public boolean needsNextQuestion(LocalDateTime bothAnsweredAt) {
        if (bothAnsweredAt == null) {
            return false;
        }

        LocalDate yesterday = LocalDateTime.now().minusDays(1).toLocalDate();

        return bothAnsweredAt.toLocalDate().equals(yesterday);
    }
}
