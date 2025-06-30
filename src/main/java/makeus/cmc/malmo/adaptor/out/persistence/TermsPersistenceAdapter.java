package makeus.cmc.malmo.adaptor.out.persistence;

import lombok.RequiredArgsConstructor;
import makeus.cmc.malmo.adaptor.out.persistence.mapper.TermsMapper;
import makeus.cmc.malmo.adaptor.out.persistence.repository.TermsRepository;
import makeus.cmc.malmo.application.port.out.LoadTermsPort;
import makeus.cmc.malmo.domain.model.terms.Terms;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class TermsPersistenceAdapter implements LoadTermsPort {

    private final TermsRepository termsRepository;
    private final TermsMapper termsMapper;

    @Override
    public Optional<Terms> loadTermsById(Long termsId) {
        return termsRepository.findById(termsId)
                .map(termsMapper::toDomain);
    }
}
