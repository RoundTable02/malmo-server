package makeus.cmc.malmo.application.port.in;

import lombok.Builder;
import lombok.Data;

public interface SignInUseCase {

    TokenResponse signInKakao(SignInKakaoCommand command);

    @Data
    @Builder
    class SignInKakaoCommand {
        private String idToken;
    }

    @Data
    @Builder
    class TokenResponse {
        private String grantType;
        private String accessToken;
        private String refreshToken;
    }
}