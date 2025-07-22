package makeus.cmc.malmo.domain.value.id;

import lombok.Value;

@Value
public class CoupleQuestionId {
    Long value;

    public static CoupleQuestionId of(Long value) {
        return new CoupleQuestionId(value);
    }
}
