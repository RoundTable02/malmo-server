package makeus.cmc.malmo.domain.model.question;

import lombok.Getter;
import lombok.experimental.SuperBuilder;
import lombok.AllArgsConstructor;
import makeus.cmc.malmo.domain.model.BaseTimeEntity;
import makeus.cmc.malmo.domain.model.couple.CoupleMember;

@Getter
@SuperBuilder
@AllArgsConstructor
public class MemberAnswer extends BaseTimeEntity {
    private Long id;
    private CoupleQuestion coupleQuestion;
    private CoupleMember coupleMember;
    private String answer;
    private MemberAnswerState memberAnswerState;
}