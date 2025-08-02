package makeus.cmc.malmo.domain.model.question;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import makeus.cmc.malmo.domain.value.id.MemberId;
import makeus.cmc.malmo.domain.value.state.CoupleQuestionState;

import java.time.LocalDateTime;

@Getter
@Builder(access = AccessLevel.PRIVATE)
public class TempCoupleQuestion {
    private Long id;
    private Question question;
    private MemberId memberId;
    private String answer;

    private CoupleQuestionState coupleQuestionState;

    // BaseTimeEntity fields
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;
    private LocalDateTime deletedAt;

    public static TempCoupleQuestion create(MemberId memberId, Question question) {
        return TempCoupleQuestion.builder()
                .question(question)
                .memberId(memberId)
                .coupleQuestionState(CoupleQuestionState.ALIVE)
                .build();
    }

    public boolean isAnswered() {
        return answer != null && !answer.isEmpty();
    }

    public void answerQuestion(String answer) {
        this.answer = answer;
    }

    public void updateAnswer(String answer) {
        this.answer = answer;
    }

    public void usedForCoupleQuestion() {
        this.coupleQuestionState = CoupleQuestionState.DELETED;
    }

    public static TempCoupleQuestion from(Long id, Question question, MemberId memberId, String answer, CoupleQuestionState coupleQuestionState, LocalDateTime createdAt, LocalDateTime modifiedAt, LocalDateTime deletedAt) {
        return TempCoupleQuestion.builder()
                .id(id)
                .question(question)
                .memberId(memberId)
                .answer(answer)
                .coupleQuestionState(coupleQuestionState)
                .createdAt(createdAt)
                .modifiedAt(modifiedAt)
                .deletedAt(deletedAt)
                .build();
    }
}