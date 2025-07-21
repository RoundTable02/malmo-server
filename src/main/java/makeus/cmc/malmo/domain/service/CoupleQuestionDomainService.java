package makeus.cmc.malmo.domain.service;

import lombok.Data;
import makeus.cmc.malmo.domain.model.question.CoupleQuestion;
import makeus.cmc.malmo.domain.model.question.TempCoupleQuestion;
import makeus.cmc.malmo.domain.value.id.CoupleId;
import makeus.cmc.malmo.domain.value.id.CoupleQuestionId;
import makeus.cmc.malmo.domain.value.id.MemberId;
import makeus.cmc.malmo.domain.value.state.CoupleQuestionState;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
public class CoupleQuestionDomainService {

    public CoupleQuestion getMaxLevelQuestion(CoupleId coupleId) {
        return null;
    }

    public QuestionRepositoryDto getMaxLevelQuestionDto(CoupleId coupleId) {
        return null;
    }

    public TempCoupleQuestion getTempCoupleQuestion(MemberId memberId) {
        return null;
    }

    @Transactional
    public CoupleQuestion createNextCoupleQuestion(CoupleId coupleId, int level) {
        return null;
    }

    public QuestionRepositoryDto getCoupleQuestionByLevelDto(CoupleId coupleId, int level) {
        return null;
    }

    public void validateQuestionOwnership(CoupleQuestionId coupleQuestionId, MemberId memberId) {
    }

    public AnswersRepositoryDto getQuestionAnswers(CoupleQuestionId coupleQuestionId) {
        return null;
    }

    @Transactional
    public void answerQuestion(CoupleQuestion coupleQuestion, MemberId memberId, String answer) {

    }

    @Transactional
    public void answerQuestion(TempCoupleQuestion coupleQuestion, MemberId memberId, String answer) {

    }

    public long countAnswers(CoupleQuestionId coupleQuestionId) {
        return 0;
    }

    @Transactional
    public void updateQuestionComplete(CoupleQuestion coupleQuestion) {

    }

    @Transactional
    public void updateAnswer(CoupleQuestion coupleQuestion, MemberId memberId, String answer) {

    }

    @Transactional
    public void updateAnswer(TempCoupleQuestion coupleQuestion, MemberId memberId, String answer) {

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

    @Data
    public class AnswersRepositoryDto {
        private AnswerRepositoryDto me;
        private AnswerRepositoryDto partner;
    }

    @Data
    public class AnswerRepositoryDto {
        private String nickname;
        private String answer;
        private boolean updatable;
    }


    public boolean needsNextQuestion(LocalDateTime bothAnsweredAt) {
        if (bothAnsweredAt == null) {
            return false;
        }

        LocalDate yesterday = LocalDateTime.now().minusDays(1).toLocalDate();

        return bothAnsweredAt.toLocalDate().equals(yesterday);
    }
}
