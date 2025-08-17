package makeus.cmc.malmo.integration_test.dto_factory;

import makeus.cmc.malmo.adaptor.in.web.controller.LoginController;
import makeus.cmc.malmo.adaptor.in.web.controller.RefreshTokenController;

public class SignInRequestDtoFactory {

    public static LoginController.KakaoLoginRequestDto createKakaoLoginRequestDto(String idToken, String accessToken) {
        LoginController.KakaoLoginRequestDto dto = new LoginController.KakaoLoginRequestDto();
        dto.setIdToken(idToken);
        dto.setAccessToken(accessToken);
        return dto;
    }

    public static LoginController.AppleLoginRequestDto createAppleLoginRequestDto(String idToken, String authorizationCode) {
        LoginController.AppleLoginRequestDto dto = new LoginController.AppleLoginRequestDto();
        dto.setIdToken(idToken);
        dto.setAuthorizationCode(authorizationCode);
        return dto;
    }

    public static RefreshTokenController.RefreshRequestDto createRefreshRequestDto(String refreshToken) {
        RefreshTokenController.RefreshRequestDto dto = new RefreshTokenController.RefreshRequestDto();
        dto.setRefreshToken(refreshToken);
        return dto;
    }
}
