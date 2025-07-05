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
public class LoveTypeEntityId {
    @Column(name = "love_type_entity_id")
    Long value;

    public static LoveTypeEntityId of(Long value) {
        return new LoveTypeEntityId(value);
    }
}
