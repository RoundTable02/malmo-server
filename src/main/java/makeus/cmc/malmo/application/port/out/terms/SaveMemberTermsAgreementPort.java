package makeus.cmc.malmo.application.port.out.terms;

import makeus.cmc.malmo.domain.model.terms.MemberTermsAgreement;

public interface SaveMemberTermsAgreementPort {
    void saveMemberTermsAgreement(MemberTermsAgreement memberTermsAgreement);
}
