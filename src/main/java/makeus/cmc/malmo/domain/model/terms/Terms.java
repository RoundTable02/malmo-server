package makeus.cmc.malmo.domain.model.terms;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.SuperBuilder;
import makeus.cmc.malmo.domain.model.BaseTimeEntity;
import makeus.cmc.malmo.domain.value.type.TermsType;

@Getter
@SuperBuilder
@AllArgsConstructor
public class Terms extends BaseTimeEntity {
    private final Long id;
    private final String title;
    private final String content;
    private final float version;
    private final boolean isRequired;
    private final TermsType termsType;
}