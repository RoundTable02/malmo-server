package makeus.cmc.malmo.domain.model.value;

import lombok.Value;

@Value
public class MemberId {
    Long value;

    public static MemberId of(Long value) {
        return new MemberId(value);
    }
}
