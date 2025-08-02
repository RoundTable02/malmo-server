package makeus.cmc.malmo.application.service;

import lombok.RequiredArgsConstructor;
import makeus.cmc.malmo.adaptor.in.aop.CheckValidMember;
import makeus.cmc.malmo.application.port.in.TermsUseCase;
import makeus.cmc.malmo.application.service.helper.terms.TermsQueryHelper;
import makeus.cmc.malmo.domain.model.terms.Terms;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class TermsService implements TermsUseCase {

    private final TermsQueryHelper termsQueryHelper;

    @Override
    @CheckValidMember
    public TermsListResponse getTerms() {
        List<TermsDto> termsDtos = termsQueryHelper.getLatestTerms().stream()
                .map(this::toResponseDto)
                .toList();

        return TermsListResponse.builder()
                .termsList(termsDtos)
                .build();
    }

    private TermsDto toResponseDto(Terms term) {
        return TermsDto.builder()
                .termsType(term.getTermsType())
                .content(TermsUseCase.TermsContentDto.builder()
                        .termsId(term.getId())
                        .title(term.getTitle())
                        .content(term.getContent())
                        .version(term.getVersion())
                        .isRequired(term.isRequired())
                        .build())
                .build();
    }
}
