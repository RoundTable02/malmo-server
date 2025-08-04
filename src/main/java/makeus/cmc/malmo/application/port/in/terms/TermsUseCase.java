package makeus.cmc.malmo.application.port.in.terms;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import makeus.cmc.malmo.domain.value.state.TermsDetailsType;
import makeus.cmc.malmo.domain.value.type.TermsType;

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
        private TermsType termsType;
        private TermsContentDto content;
    }

    @Data
    @Builder
    class TermsContentDto {
        private Long termsId;
        private String title;
        private List<TermsDetailsDto> details;
        private float version;
        @JsonProperty("isRequired")
        private boolean isRequired;
    }

    @Data
    @Builder
    class TermsDetailsDto {
        private TermsDetailsType type;
        private String content;
    }
}
