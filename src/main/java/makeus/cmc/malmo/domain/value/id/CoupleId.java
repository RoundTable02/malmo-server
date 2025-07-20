package makeus.cmc.malmo.domain.value.id;

import lombok.Value;

@Value
public class CoupleId {
    Long value;

    public static CoupleId of(Long value) {
        return new CoupleId(value);
    }
}
