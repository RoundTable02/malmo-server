package makeus.cmc.malmo.domain.model.terms;

import lombok.Getter;
import lombok.experimental.SuperBuilder;
import lombok.AllArgsConstructor;
import makeus.cmc.malmo.domain.model.BaseTimeEntity;

@Getter
@SuperBuilder
@AllArgsConstructor
public class Terms extends BaseTimeEntity {
    private final Long id;
    private final String title;
    private final String content;
    private final String version;
    private final boolean isRequired;
}