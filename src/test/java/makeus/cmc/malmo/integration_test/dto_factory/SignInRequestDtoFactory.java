package makeus.cmc.malmo.integration_test.dto_factory;

import makeus.cmc.malmo.adaptor.in.web.controller.LoginController;

public class SignInRequestDtoFactory {

    public static LoginController.KakaoLoginRequestDto createKakaoLoginRequestDto(String idToken, String accessToken) {
        LoginController.KakaoLoginRequestDto dto = new LoginController.KakaoLoginRequestDto();
        dto.setIdToken(idToken);
        dto.setAccessToken(accessToken);
        return dto;
    }

    public static LoginController.AppleLoginRequestDto createAppleLoginRequestDto(String idToken) {
        LoginController.AppleLoginRequestDto dto = new LoginController.AppleLoginRequestDto();
        dto.setIdToken(idToken);
        return dto;
    }
}
