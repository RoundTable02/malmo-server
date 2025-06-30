package makeus.cmc.malmo.adaptor.in.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import makeus.cmc.malmo.adaptor.in.web.docs.ApiCommonResponses;
import makeus.cmc.malmo.adaptor.in.web.dto.BaseResponse;
import makeus.cmc.malmo.application.port.in.SignInUseCase;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "소셜 로그인 API", description = "OIDC ID Token 기반의 로그인 API")
@Slf4j
@RestController
@RequiredArgsConstructor
public class LoginController {

    private final SignInUseCase signInUseCase;

    @Operation(
            summary = "카카오 소셜 로그인",
            description = "카카오 OIDC ID 토큰을 통해 로그인합니다. 신규 사용자의 경우 자동으로 회원가입이 진행됩니다."
    )
    @ApiCommonResponses.Login
    @PostMapping("/login/kakao")
    public BaseResponse<SignInUseCase.SignInResponse> loginWithKakao(@RequestBody KakaoLoginRequestDto requestDto) {
        SignInUseCase.SignInKakaoCommand command = SignInUseCase.SignInKakaoCommand.builder()
                .idToken(requestDto.idToken)
                .accessToken(requestDto.accessToken)
                .build();
        return BaseResponse.success(signInUseCase.signInKakao(command));
    }

    @Operation(
            summary = "애플 소셜 로그인",
            description = "애플 OIDC ID 토큰을 통해 로그인합니다. 신규 사용자의 경우 자동으로 회원가입이 진행됩니다."
    )
    @ApiCommonResponses.Login
    @PostMapping("/login/apple")
    public BaseResponse<SignInUseCase.SignInResponse> loginWithApple(@RequestBody AppleLoginRequestDto requestDto) {
        SignInUseCase.SignInAppleCommand command = SignInUseCase.SignInAppleCommand.builder()
                .idToken(requestDto.idToken)
                .build();
        return BaseResponse.success(signInUseCase.signInApple(command));
    }


    @Data
    public static class KakaoLoginRequestDto {
        private String idToken;
        private String accessToken;
    }

    @Data
    public static class AppleLoginRequestDto {
        private String idToken;
    }
}
