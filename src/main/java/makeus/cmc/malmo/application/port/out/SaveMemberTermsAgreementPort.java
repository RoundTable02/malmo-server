package makeus.cmc.malmo.application.port.out;

import makeus.cmc.malmo.domain.model.terms.MemberTermsAgreement;

public interface SaveMemberTermsAgreementPort {
    void saveMemberTermsAgreement(MemberTermsAgreement memberTermsAgreement);
}
