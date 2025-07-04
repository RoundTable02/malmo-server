package makeus.cmc.malmo.application.port.in;

import lombok.Builder;
import lombok.Data;

import java.util.List;

public interface UpdateTermsAgreementUseCase {

    TermsAgreementResponse updateTermsAgreement(TermsAgreementCommand command);

    @Data
    @Builder
    class TermsAgreementCommand {
        private Long memberId;
        private List<TermsDto> terms;
    }

    @Data
    @Builder
    class TermsAgreementResponse {
        private List<TermsDto> terms;
    }

    @Data
    @Builder
    class TermsDto {
        private Long termsId;
        private Boolean isAgreed;
    }
}