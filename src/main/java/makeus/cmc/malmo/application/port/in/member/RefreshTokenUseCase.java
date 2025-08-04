package makeus.cmc.malmo.application.port.in.member;

import lombok.Builder;
import lombok.Data;

public interface RefreshTokenUseCase {

    TokenResponse refreshToken(RefreshTokenCommand command);

    @Data
    @Builder
    class RefreshTokenCommand {
        private String refreshToken;
    }

    @Data
    @Builder
    class TokenResponse {
        private String grantType;
        private String accessToken;
        private String refreshToken;
    }
}