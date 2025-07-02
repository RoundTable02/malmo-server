package makeus.cmc.malmo.application.port.in;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

public interface SignUpUseCase {

    SignUpResponse signUp(SignUpCommand command);

    @Data
    @Builder
    class SignUpCommand {
        private Long memberId;
        private List<TermsCommand> terms;
        private String nickname;
        private LocalDate loveStartDate;
    }

    @Data
    @Builder
    class TermsCommand {
        private Long termsId;
        private Boolean isAgreed;
    }

    @Data
    @Builder
    class SignUpResponse {
        private String coupleCode;
    }
}