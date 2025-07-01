package makeus.cmc.malmo.domain.model.value;

import lombok.*;

@Value
public class CoupleId {
    Long value;

    public static CoupleId of(Long value) {
        return new CoupleId(value);
    }
}
