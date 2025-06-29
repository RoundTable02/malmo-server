package makeus.cmc.malmo.adaptor.in.web.controller;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import makeus.cmc.malmo.adaptor.in.web.dto.BaseResponse;
import makeus.cmc.malmo.application.port.in.RefreshTokenUseCase;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class RefreshTokenController {

    private final RefreshTokenUseCase refreshTokenUseCase;

    @PostMapping("/refresh")
    public BaseResponse<RefreshTokenUseCase.TokenResponse> refreshToken(
            @RequestBody RefreshRequestDto requestDto,
            @AuthenticationPrincipal User user
    ) {
        RefreshTokenUseCase.RefreshTokenCommand command = RefreshTokenUseCase.RefreshTokenCommand.builder()
                .memberId(Long.parseLong(user.getUsername()))
                .refreshToken(requestDto.refreshToken)
                .build();
        return BaseResponse.success(refreshTokenUseCase.refreshToken(command));
    }

    @Data
    public static class RefreshRequestDto {
        private String refreshToken;
    }
}
