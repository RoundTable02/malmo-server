package makeus.cmc.malmo.domain.value.id;

import lombok.Value;

@Value
public class LoveTypeId {
    Long value;

    public static LoveTypeId of(Long value) {
        return new LoveTypeId(value);
    }
}
