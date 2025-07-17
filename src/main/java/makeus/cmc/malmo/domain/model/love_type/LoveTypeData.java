package makeus.cmc.malmo.domain.model.love_type;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class LoveTypeData {
    private LoveTypeCategory category;
    private String name;
    private String loveTypeName;
    private String imageUrl;
    private String summary;
    private String description;
    private String[] relationshipAttitudes;
    private String[] problemSolvingAttitudes;
    private String[] emotionalExpressions;
}
