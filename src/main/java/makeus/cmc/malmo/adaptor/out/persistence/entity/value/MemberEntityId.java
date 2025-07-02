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
public class MemberEntityId {
    @Column(name = "member_id")
    Long value;

    public static MemberEntityId of(Long value) {
        return new MemberEntityId(value);
    }
}
