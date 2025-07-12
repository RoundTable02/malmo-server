package makeus.cmc.malmo.domain.model.question;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import makeus.cmc.malmo.domain.model.couple.Couple;
import makeus.cmc.malmo.domain.value.state.CoupleQuestionState;

import java.time.LocalDateTime;

@Getter
@Builder(access = AccessLevel.PRIVATE)
public class CoupleQuestion {
    private Long id;
    private Question question;
    private Couple couple;
    private CoupleQuestionState coupleQuestionState;

    // BaseTimeEntity fields
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;
    private LocalDateTime deletedAt;
}