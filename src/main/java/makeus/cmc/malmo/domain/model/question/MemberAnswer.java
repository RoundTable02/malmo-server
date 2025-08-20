package makeus.cmc.malmo.domain.model.question;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import makeus.cmc.malmo.domain.value.id.CoupleQuestionId;
import makeus.cmc.malmo.domain.value.id.MemberId;
import makeus.cmc.malmo.domain.value.state.MemberAnswerState;

import java.time.LocalDateTime;

@Getter
@Builder(access = AccessLevel.PACKAGE)
public class MemberAnswer {
    private Long id;
    private CoupleQuestionId coupleQuestionId;
    private MemberId memberId;
    private String answer;
    private MemberAnswerState memberAnswerState;

    // BaseTimeEntity fields
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;
    private LocalDateTime deletedAt;

    public void updateAnswer(String answer) {
        this.answer = answer;
    }

    public static MemberAnswer from(Long id, CoupleQuestionId coupleQuestionId, MemberId memberId, String answer, MemberAnswerState memberAnswerState, LocalDateTime createdAt, LocalDateTime modifiedAt, LocalDateTime deletedAt) {
        return MemberAnswer.builder()
                .id(id)
                .coupleQuestionId(coupleQuestionId)
                .memberId(memberId)
                .answer(answer)
                .memberAnswerState(memberAnswerState)
                .createdAt(createdAt)
                .modifiedAt(modifiedAt)
                .deletedAt(deletedAt)
                .build();
    }
}