package makeus.cmc.malmo.domain.model.love_type;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.SuperBuilder;
import makeus.cmc.malmo.domain.model.BaseTimeEntity;

@Getter
@SuperBuilder
@AllArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class LoveTypeQuestion extends BaseTimeEntity {

    private Long id;
    private int questionNumber;
    private boolean isReversed;
    private String content;
    private LoveTypeQuestionType loveTypeQuestionType;
    private int weight;
}
