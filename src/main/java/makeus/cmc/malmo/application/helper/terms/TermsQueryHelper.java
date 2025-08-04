package makeus.cmc.malmo.application.helper.terms;

import lombok.RequiredArgsConstructor;
import makeus.cmc.malmo.application.port.out.terms.LoadTermsAgreementPort;
import makeus.cmc.malmo.application.port.out.terms.LoadTermsPort;
import makeus.cmc.malmo.application.exception.TermsNotFoundException;
import makeus.cmc.malmo.domain.model.terms.MemberTermsAgreement;
import makeus.cmc.malmo.domain.model.terms.Terms;
import makeus.cmc.malmo.domain.model.terms.TermsDetails;
import makeus.cmc.malmo.domain.value.id.MemberId;
import makeus.cmc.malmo.domain.value.id.TermsId;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class TermsQueryHelper {

    private final LoadTermsPort loadTermsPort;
    private final LoadTermsAgreementPort loadTermsAgreementPort;

    public Terms getTermsByIdOrThrow(Long termsId) {
        return loadTermsPort.loadTermsById(termsId)
                .orElseThrow(TermsNotFoundException::new);
    }

    public MemberTermsAgreement getTermsAgreementOrThrow(MemberId memberId, TermsId termsId) {
        return loadTermsAgreementPort.loadTermsAgreementByMemberIdAndTermsId(memberId, termsId)
                .orElseThrow(TermsNotFoundException::new);
    }

    public List<Terms> getLatestTerms() {
        return loadTermsPort.loadLatestTerms();
    }

    public List<TermsDetails> getTermsDetailsByTermsId(TermsId termsId) {
        return loadTermsPort.loadTermsDetailsByTermsId(termsId);
    }
}
