package makeus.cmc.malmo.application.helper.terms;

import lombok.RequiredArgsConstructor;
import makeus.cmc.malmo.application.port.out.terms.SaveMemberTermsAgreementPort;
import makeus.cmc.malmo.domain.model.terms.MemberTermsAgreement;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TermsCommandHelper {

    private final SaveMemberTermsAgreementPort saveMemberTermsAgreementPort;

    public void saveMemberTermsAgreement(MemberTermsAgreement memberTermsAgreement) {
        saveMemberTermsAgreementPort.saveMemberTermsAgreement(memberTermsAgreement);
    }
}
