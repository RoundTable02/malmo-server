package makeus.cmc.malmo.domain.model.love_type;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
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
