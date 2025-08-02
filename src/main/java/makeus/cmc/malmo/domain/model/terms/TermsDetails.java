package makeus.cmc.malmo.domain.model.terms;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import makeus.cmc.malmo.domain.value.id.TermsId;
import makeus.cmc.malmo.domain.value.state.TermsDetailsType;

@Getter
@Builder(access = AccessLevel.PRIVATE)
public class TermsDetails {

    private final Long id;

    private final TermsId termsId;

    private final TermsDetailsType termsDetailsType;

    private final String content;

    public static TermsDetails from(Long id, TermsId termsId, TermsDetailsType termsDetailsType, String content) {
        return TermsDetails.builder()
                .id(id)
                .termsId(termsId)
                .termsDetailsType(termsDetailsType)
                .content(content)
                .build();
    }
}
