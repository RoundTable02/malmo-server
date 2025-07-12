package makeus.cmc.malmo.domain.model.question;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.SuperBuilder;
import makeus.cmc.malmo.domain.model.BaseTimeEntity;
import makeus.cmc.malmo.domain.model.couple.Couple;
import makeus.cmc.malmo.domain.value.state.CoupleQuestionState;

@Getter
@SuperBuilder
@AllArgsConstructor
public class CoupleQuestion extends BaseTimeEntity {
    private Long id;
    private Question question;
    private Couple couple;
    private CoupleQuestionState coupleQuestionState;
}