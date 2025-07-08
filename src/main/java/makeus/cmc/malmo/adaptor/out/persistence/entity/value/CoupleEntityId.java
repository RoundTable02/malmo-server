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
public class CoupleEntityId {
    @Column(name = "couple_id", nullable = false)
    Long value;

    public static CoupleEntityId of(Long value) {
        return new CoupleEntityId(value);
    }
}
