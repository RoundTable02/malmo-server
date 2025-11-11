package makeus.cmc.malmo.application.port.in.member;

import lombok.Builder;
import lombok.Data;

import java.util.List;

public interface SignUpUseCase {

    void signUp(SignUpCommand command);

    @Data
    @Builder
    class SignUpCommand {
        private Long memberId;
        private List<TermsCommand> terms;
        private String nickname;
        private Long loveTypeId;
    }

    @Data
    @Builder
    class TermsCommand {
        private Long termsId;
        private Boolean isAgreed;
    }
}