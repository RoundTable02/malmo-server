package makeus.cmc.malmo.application.service;

import lombok.RequiredArgsConstructor;
import makeus.cmc.malmo.application.port.in.TermsUseCase;
import makeus.cmc.malmo.application.port.out.LoadTermsPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class TermsService implements TermsUseCase {

    private final LoadTermsPort loadTermsPort;

    @Override
    public TermsListResponse getTerms() {
        List<TermsDto> termsDtos = loadTermsPort.loadLatestTerms().stream()
                .map(term -> TermsDto.builder()
                        .termsType(term.getTermsType())
                        .content(TermsUseCase.TermsContentDto.builder()
                                .termsId(term.getId())
                                .title(term.getTitle())
                                .content(term.getContent())
                                .version(term.getVersion())
                                .isRequired(term.isRequired())
                                .build())
                        .build())
                .toList();

        return TermsListResponse.builder()
                .termsList(termsDtos)
                .build();
    }
}
