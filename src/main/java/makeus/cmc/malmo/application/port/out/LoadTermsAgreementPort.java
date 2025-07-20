package makeus.cmc.malmo.application.port.out;

import makeus.cmc.malmo.domain.model.terms.MemberTermsAgreement;
import makeus.cmc.malmo.domain.value.id.MemberId;
import makeus.cmc.malmo.domain.value.id.TermsId;

import java.util.Optional;

public interface LoadTermsAgreementPort {

    Optional<MemberTermsAgreement> loadTermsAgreementByMemberIdAndTermsId(MemberId memberId, TermsId termsId);
}
