package makeus.cmc.malmo.application.port.in;

import lombok.Builder;
import lombok.Data;
import makeus.cmc.malmo.adaptor.out.jwt.TokenInfo;

public interface SignInUseCase {
    TokenInfo signIn(SignInCommand command);

    @Data
    @Builder
    class SignInCommand {
        private String provider;
        private String providerId;
    }
}