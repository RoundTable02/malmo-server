package makeus.cmc.malmo.application.port.in.member;

import lombok.Builder;
import lombok.Data;

public interface SignInUseCase {

    SignInResponse signInKakao(SignInKakaoCommand command);
    SignInResponse signInApple(SignInAppleCommand command);

    @Data
    @Builder
    class SignInKakaoCommand {
        private String idToken;
        private String accessToken;
        private String deviceId;
    }

    @Data
    @Builder
    class SignInAppleCommand {
        private String idToken;
        private String authorizationCode;
        private String deviceId;
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