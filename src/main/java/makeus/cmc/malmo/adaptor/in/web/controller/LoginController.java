package makeus.cmc.malmo.adaptor.in.web.controller;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import makeus.cmc.malmo.adaptor.in.web.dto.BaseResponse;
import makeus.cmc.malmo.application.port.in.RefreshTokenUseCase;
import makeus.cmc.malmo.application.port.in.SignInUseCase;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class LoginController {

    // 로그인을 위한 API
    // OIDC ID 토큰을 받아서 회원 가입 및 로그인 처리
    // 사용자가 없는 경우 자동 회원 가입

    private final SignInUseCase signInUseCase;

    @PostMapping("/login/kakao")
    public BaseResponse<SignInUseCase.TokenResponse> login(@RequestBody LoginRequestDto requestDto) {
        SignInUseCase.SignInKakaoCommand command = SignInUseCase.SignInKakaoCommand.builder()
                .idToken(requestDto.idToken)
                .build();
        return BaseResponse.success(signInUseCase.signInKakao(command));
    }

    @Data
    public static class LoginRequestDto {
        private String idToken;
    }
}
