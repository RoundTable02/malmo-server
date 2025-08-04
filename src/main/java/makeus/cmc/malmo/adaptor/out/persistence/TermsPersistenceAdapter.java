package makeus.cmc.malmo.adaptor.out.persistence;

import lombok.RequiredArgsConstructor;
import makeus.cmc.malmo.adaptor.out.persistence.entity.terms.TermsDetailsEntity;
import makeus.cmc.malmo.adaptor.out.persistence.mapper.TermsMapper;
import makeus.cmc.malmo.adaptor.out.persistence.repository.TermsDetailsRepository;
import makeus.cmc.malmo.adaptor.out.persistence.repository.TermsRepository;
import makeus.cmc.malmo.application.port.out.terms.LoadTermsPort;
import makeus.cmc.malmo.domain.model.terms.Terms;
import makeus.cmc.malmo.domain.model.terms.TermsDetails;
import makeus.cmc.malmo.domain.value.id.TermsId;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class TermsPersistenceAdapter implements LoadTermsPort {

    private final TermsRepository termsRepository;
    private final TermsDetailsRepository termsDetailsRepository;
    private final TermsMapper termsMapper;

    @Override
    public Optional<Terms> loadTermsById(Long termsId) {
        return termsRepository.findById(termsId)
                .map(termsMapper::toDomain);
    }

    @Override
    public List<Terms> loadLatestTerms() {
        return termsRepository.findLatestTermsForAllTypes().stream()
                .map(termsMapper::toDomain)
                .toList();
    }

    @Override
    public List<TermsDetails> loadTermsDetailsByTermsId(TermsId termsId) {
        List<TermsDetailsEntity> entityList = termsDetailsRepository.getTermsDetailsEntitiesByTermsEntityId(termsId.getValue());
        return entityList.stream()
                .map(termsMapper::toDomain)
                .toList();
    }
}
