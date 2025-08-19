package makeus.cmc.malmo.domain.model.couple;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import makeus.cmc.malmo.domain.value.id.MemberId;
import makeus.cmc.malmo.domain.value.type.LoveTypeCategory;

@AllArgsConstructor(access = AccessLevel.PACKAGE)
public class CoupleMemberSnapshot {
    private MemberId memberId;
    private String nickname;
    private LoveTypeCategory loveTypeCategory;
    private float avoidanceRate;
    private float anxietyRate;
}
