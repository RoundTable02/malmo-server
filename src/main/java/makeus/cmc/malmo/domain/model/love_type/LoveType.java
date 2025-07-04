package makeus.cmc.malmo.domain.model.love_type;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.SuperBuilder;
import makeus.cmc.malmo.domain.model.BaseTimeEntity;

@Getter
@SuperBuilder
@AllArgsConstructor
public class LoveType extends BaseTimeEntity {
    private Long id;
    private String title;
    private String content;
    private String imageUrl;
    private float weight;
}