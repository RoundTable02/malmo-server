package makeus.cmc.malmo.application.service.terms;

import lombok.RequiredArgsConstructor;
import makeus.cmc.malmo.application.port.in.terms.TermsUseCase;
import makeus.cmc.malmo.application.helper.terms.TermsQueryHelper;
import makeus.cmc.malmo.domain.model.terms.Terms;
import makeus.cmc.malmo.domain.model.terms.TermsDetails;
import makeus.cmc.malmo.domain.value.id.TermsId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class TermsService implements TermsUseCase {

    private final TermsQueryHelper termsQueryHelper;

    @Override
    public TermsListResponse getTerms() {
        List<TermsDto> termsDtos = termsQueryHelper.getLatestTerms().stream()
                .map(terms -> {
                    List<TermsDetails> termsDetails = termsQueryHelper.getTermsDetailsByTermsId(TermsId.of(terms.getId()));
                    return toResponseDto(terms, termsDetails);
                })
                .toList();

        return TermsListResponse.builder()
                .termsList(termsDtos)
                .build();
    }

    private TermsDto toResponseDto(Terms term, List<TermsDetails> termsDetails) {
        List<TermsDetailsDto> details = termsDetails.stream()
                .map(this::toDetailsDto)
                .toList();

        return TermsDto.builder()
                .termsType(term.getTermsType())
                .content(TermsUseCase.TermsContentDto.builder()
                        .termsId(term.getId())
                        .title(term.getTitle())
                        .details(details)
                        .version(term.getVersion())
                        .isRequired(term.isRequired())
                        .build())
                .build();
    }

    private TermsDetailsDto toDetailsDto(TermsDetails details) {
        return TermsUseCase.TermsDetailsDto.builder()
                .type(details.getTermsDetailsType())
                .content(details.getContent())
                .build();
    }
}
