package makeus.cmc.malmo.adaptor.in.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import makeus.cmc.malmo.adaptor.in.web.docs.ApiCommonResponses;
import makeus.cmc.malmo.adaptor.in.web.docs.SwaggerResponses;
import makeus.cmc.malmo.adaptor.in.web.dto.BaseResponse;
import makeus.cmc.malmo.application.port.in.member.SignInUseCase;
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
    @ApiResponse(
            responseCode = "200",
            description = "로그인 성공",
            content = @Content(schema = @Schema(implementation = SwaggerResponses.LoginSuccessResponse.class))
    )
    @ApiCommonResponses.Login
    @PostMapping("/login/kakao")
    public BaseResponse<SignInUseCase.SignInResponse> loginWithKakao(
            @Valid @RequestBody KakaoLoginRequestDto requestDto
    ) {
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
    @ApiResponse(
            responseCode = "200",
            description = "로그인 성공",
            content = @Content(schema = @Schema(implementation = SwaggerResponses.LoginSuccessResponse.class))
    )
    @ApiCommonResponses.Login
    @PostMapping("/login/apple")
    public BaseResponse<SignInUseCase.SignInResponse> loginWithApple(
            @Valid @RequestBody AppleLoginRequestDto requestDto
    ) {
        SignInUseCase.SignInAppleCommand command = SignInUseCase.SignInAppleCommand.builder()
                .idToken(requestDto.idToken)
                .build();
        return BaseResponse.success(signInUseCase.signInApple(command));
    }


    @Data
    public static class KakaoLoginRequestDto {
        @NotEmpty(message = "idToken은 필수 입력값입니다.")
        private String idToken;
        @NotEmpty(message = "accessToken은 필수 입력값입니다.")
        private String accessToken;
    }

    @Data
    public static class AppleLoginRequestDto {
        @NotEmpty(message = "idToken은 필수 입력값입니다.")
        private String idToken;
    }
}
