package makeus.cmc.malmo.domain.value.id;

import lombok.Value;

@Value
public class InviteCodeValue {
    String value;

    public static InviteCodeValue of(String value) {
        return new InviteCodeValue(value);
    }
}
