package makeus.cmc.malmo.domain.model.love_type;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import makeus.cmc.malmo.domain.model.BaseTimeEntity;

@Getter
@NoArgsConstructor
public class LoveTypeQuestionData {

    private Long id;
    private int questionNumber;
    @JsonProperty("isReversed")
    private boolean isReversed;
    private String content;
    private LoveTypeQuestionType loveTypeQuestionType;
    private int weight;

    public boolean isAnxietyType() {
        return loveTypeQuestionType == LoveTypeQuestionType.ANXIETY;
    }

    public int getScore(int score) {
        if (isReversed) {
            return 6 - score;
        }
        return score;
    }
}
