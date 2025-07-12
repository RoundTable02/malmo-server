package makeus.cmc.malmo.domain.model.terms;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.SuperBuilder;
import makeus.cmc.malmo.domain.model.BaseTimeEntity;
import makeus.cmc.malmo.domain.value.id.MemberId;
import makeus.cmc.malmo.domain.value.id.TermsId;

@Getter
@SuperBuilder
@AllArgsConstructor
public class MemberTermsAgreement extends BaseTimeEntity {
    private Long id;
    private MemberId memberId;
    private TermsId termsId;
    private boolean agreed;

    public static MemberTermsAgreement signTerms(MemberId memberId, TermsId termsId, boolean agreed) {
        return MemberTermsAgreement.builder()
                .memberId(memberId)
                .termsId(termsId)
                .agreed(agreed)
                .build();
    }

    public void updateAgreement(boolean agreed) {
        this.agreed = agreed;
    }
}