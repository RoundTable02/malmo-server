package makeus.cmc.malmo.domain.model.value;

import lombok.Value;

@Value
public class InviteCodeValue {
    String value;

    public static InviteCodeValue of(String value) {
        return new InviteCodeValue(value);
    }
}
