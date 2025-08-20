package makeus.cmc.malmo.domain.model.couple;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import makeus.cmc.malmo.domain.value.type.LoveTypeCategory;

@Getter
@AllArgsConstructor(access = AccessLevel.PACKAGE)
@Builder(access = AccessLevel.PRIVATE)
public class CoupleMemberSnapshot {
    private String nickname;
    private LoveTypeCategory loveTypeCategory;
    private float avoidanceRate;
    private float anxietyRate;

    public static CoupleMemberSnapshot from(String nickname, LoveTypeCategory loveTypeCategory, float avoidanceRate, float anxietyRate) {
        return CoupleMemberSnapshot.builder()
                .nickname(nickname)
                .loveTypeCategory(loveTypeCategory)
                .avoidanceRate(avoidanceRate)
                .anxietyRate(anxietyRate)
                .build();
    }
}
