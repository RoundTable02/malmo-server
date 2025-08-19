package makeus.cmc.malmo.adaptor.out.persistence.entity.couple;

import jakarta.persistence.Embeddable;
import jakarta.persistence.Embedded;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.*;
import makeus.cmc.malmo.adaptor.out.persistence.entity.value.MemberEntityId;
import makeus.cmc.malmo.domain.value.id.MemberId;
import makeus.cmc.malmo.domain.value.type.LoveTypeCategory;

@Embeddable
@Builder
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CoupleMemberSnapshotEntity {
    private String nickname;

    @Enumerated(value = EnumType.STRING)
    private LoveTypeCategory loveTypeCategory;

    private float avoidanceRate;

    private float anxietyRate;
}
