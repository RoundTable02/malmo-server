package makeus.cmc.malmo.domain.value.id;

import lombok.Value;

@Value
public class QuestionId {
    Long value;

    public static QuestionId of(Long value) {
        return new QuestionId(value);
    }
}
