package makeus.cmc.malmo.application.port.out;

import makeus.cmc.malmo.domain.model.terms.Terms;

import java.util.List;
import java.util.Optional;

public interface LoadTermsPort {
    Optional<Terms> loadTermsById(Long termsId);
    List<Terms> loadLatestTerms();
}
