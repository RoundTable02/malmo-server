package makeus.cmc.malmo.domain.model.value;

import lombok.Value;

@Value
public class TermsId {
    Long value;

    public static TermsId of(Long value) {
        return new TermsId(value);
    }
}
