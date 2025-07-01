package makeus.cmc.malmo.application.port.in;

import lombok.Builder;
import lombok.Data;

import java.util.List;

public interface TermsUseCase {

    List<TermsResponse> getTerms();

    @Data
    @Builder
    class TermsResponse {
        private Long termsId;
        private String title;
        private String content;
        private float version;
        private boolean isRequired;
    }
}
