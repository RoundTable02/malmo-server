package makeus.cmc.malmo.application.service.helper.terms;

import lombok.RequiredArgsConstructor;
import makeus.cmc.malmo.application.port.out.SaveMemberTermsAgreementPort;
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
