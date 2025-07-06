package makeus.cmc.malmo.application.port.in;

import lombok.Builder;
import lombok.Data;

import java.util.List;

public interface TermsUseCase {

    TermsListResponse getTerms();

    @Data
    @Builder
    class TermsListResponse {
        private List<TermsDto> termsList;
    }

    @Data
    @Builder
    class TermsDto {
        private Long termsId;
        private String title;
        private String content;
        private float version;
        private boolean isRequired;
    }
}
