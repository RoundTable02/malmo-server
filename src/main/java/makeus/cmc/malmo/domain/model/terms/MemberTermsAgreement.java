package makeus.cmc.malmo.domain.model.terms;

import lombok.Getter;
import lombok.experimental.SuperBuilder;
import lombok.AllArgsConstructor;
import makeus.cmc.malmo.domain.model.BaseTimeEntity;

@Getter
@SuperBuilder
@AllArgsConstructor
public class MemberTermsAgreement extends BaseTimeEntity {
    private Long id;
    private Long memberId;
    private Long termsId;
    private boolean agreed;

    public static MemberTermsAgreement signTerms(Long memberId, Long termsId, boolean agreed) {
        return MemberTermsAgreement.builder()
                .memberId(memberId)
                .termsId(termsId)
                .agreed(agreed)
                .build();
    }
}