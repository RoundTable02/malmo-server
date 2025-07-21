package makeus.cmc.malmo.domain.value.id;

import lombok.Value;

@Value
public class CoupleMemberId {
    Long value;

    public static CoupleMemberId of(Long value) {
        return new CoupleMemberId(value);
    }
}
