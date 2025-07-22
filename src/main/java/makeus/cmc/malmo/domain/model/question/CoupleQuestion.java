package makeus.cmc.malmo.domain.model.question;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import makeus.cmc.malmo.domain.value.id.CoupleId;
import makeus.cmc.malmo.domain.value.id.CoupleMemberId;
import makeus.cmc.malmo.domain.value.id.CoupleQuestionId;
import makeus.cmc.malmo.domain.value.state.CoupleQuestionState;
import makeus.cmc.malmo.domain.value.state.MemberAnswerState;

import java.time.LocalDateTime;

@Getter
@Builder(access = AccessLevel.PRIVATE)
public class CoupleQuestion {
    private Long id;
    private Question question;
    private CoupleId coupleId;
    private CoupleQuestionState coupleQuestionState;
    private LocalDateTime bothAnsweredAt;

    // BaseTimeEntity fields
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;
    private LocalDateTime deletedAt;

    public static CoupleQuestion createCoupleQuestion(Question question, CoupleId coupleId) {
        return CoupleQuestion.builder()
                .question(question)
                .coupleId(coupleId)
                .coupleQuestionState(CoupleQuestionState.ALIVE)
                .build();
    }

    public boolean isOwnedBy(CoupleId coupleId) {
        return this.coupleId.getValue().equals(coupleId.getValue());
    }

    public MemberAnswer createMemberAnswer(CoupleMemberId coupleMemberId, String answer) {
        return MemberAnswer.builder()
                .coupleQuestionId(CoupleQuestionId.of(this.id))
                .coupleMemberId(coupleMemberId)
                .answer(answer)
                .memberAnswerState(MemberAnswerState.ALIVE)
                .build();
    }

    public void complete() {
        this.coupleQuestionState = CoupleQuestionState.COMPLETED;
        this.bothAnsweredAt = LocalDateTime.now();
    }

    public void expire() {
        this.coupleQuestionState = CoupleQuestionState.OUTDATED;
    }

    public boolean isUpdatable() {
        return this.coupleQuestionState != CoupleQuestionState.OUTDATED;
    }

    public static CoupleQuestion from(Long id, Question question, CoupleId coupleId, CoupleQuestionState coupleQuestionState, LocalDateTime bothAnsweredAt, LocalDateTime createdAt, LocalDateTime modifiedAt, LocalDateTime deletedAt) {
        return CoupleQuestion.builder()
                .id(id)
                .question(question)
                .coupleId(coupleId)
                .coupleQuestionState(coupleQuestionState)
                .bothAnsweredAt(bothAnsweredAt)
                .createdAt(createdAt)
                .modifiedAt(modifiedAt)
                .deletedAt(deletedAt)
                .build();
    }

}