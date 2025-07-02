package makeus.cmc.malmo.domain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

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