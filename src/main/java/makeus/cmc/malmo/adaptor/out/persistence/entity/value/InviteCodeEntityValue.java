package makeus.cmc.malmo.adaptor.out.persistence.entity.value;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Embeddable
public class InviteCodeEntityValue {
    @Column(name = "invite_code", unique = true)
    String value;

    public static InviteCodeEntityValue of(String value) {
        return new InviteCodeEntityValue(value);
    }
}
