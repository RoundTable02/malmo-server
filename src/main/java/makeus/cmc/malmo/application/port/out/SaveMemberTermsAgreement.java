package makeus.cmc.malmo.application.port.out;

import makeus.cmc.malmo.domain.model.member.Member;
import makeus.cmc.malmo.domain.model.terms.MemberTermsAgreement;
import makeus.cmc.malmo.domain.model.terms.Terms;

public interface SaveMemberTermsAgreement {
    void saveMemberTermsAgreement(MemberTermsAgreement memberTermsAgreement, Member member, Terms terms);
}
