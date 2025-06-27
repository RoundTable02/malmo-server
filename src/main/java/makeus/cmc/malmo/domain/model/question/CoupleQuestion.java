package makeus.cmc.malmo.domain.model.question;

import lombok.Getter;
import lombok.experimental.SuperBuilder;
import lombok.AllArgsConstructor;
import makeus.cmc.malmo.domain.model.BaseTimeEntity;
import makeus.cmc.malmo.domain.model.couple.Couple;

@Getter
@SuperBuilder
@AllArgsConstructor
public class CoupleQuestion extends BaseTimeEntity {
    private Long id;
    private Question question;
    private Couple couple;
    private CoupleQuestionState coupleQuestionState;
}