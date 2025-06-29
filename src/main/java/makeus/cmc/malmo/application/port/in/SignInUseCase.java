package makeus.cmc.malmo.application.port.in;

import lombok.Builder;
import lombok.Data;

public interface SignInUseCase {

    SignInResponse signInKakao(SignInKakaoCommand command);

    @Data
    @Builder
    class SignInKakaoCommand {
        private String idToken;
        private String accessToken;
    }

    @Data
    @Builder
    class SignInResponse {
        private String memberState;
        private String grantType;
        private String accessToken;
        private String refreshToken;
    }
}