package makeus.cmc.malmo.domain.model.question;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import makeus.cmc.malmo.domain.value.id.CoupleId;
import makeus.cmc.malmo.domain.value.id.MemberId;
import makeus.cmc.malmo.domain.value.id.QuestionId;
import makeus.cmc.malmo.domain.value.state.CoupleQuestionState;

import java.time.LocalDateTime;

@Getter
@Builder(access = AccessLevel.PRIVATE)
public class TempCoupleQuestion {
    private Long id;
    private Question question;
    private MemberId memberId;
    private String answer;

    // BaseTimeEntity fields
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;
    private LocalDateTime deletedAt;

    public static TempCoupleQuestion create(MemberId memberId, Question question) {
        return TempCoupleQuestion.builder()
                .question(question)
                .memberId(memberId)
                .build();
    }

    public boolean isAnswered() {
        return answer != null && !answer.isEmpty();
    }

    public void answerQuestion(String answer) {
        this.answer = answer;
    }
}