package makeus.cmc.malmo.adaptor.in.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import makeus.cmc.malmo.adaptor.in.web.docs.ApiCommonResponses;
import makeus.cmc.malmo.adaptor.in.web.dto.BaseResponse;
import makeus.cmc.malmo.application.port.in.RefreshTokenUseCase;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "토큰 관리 API", description = "JWT 토큰 갱신 관련 API")
@Slf4j
@RestController
@RequiredArgsConstructor
public class RefreshTokenController {

    private final RefreshTokenUseCase refreshTokenUseCase;

    @Operation(
            summary = "JWT 토큰 갱신",
            description = "Refresh Token을 사용하여 새로운 Access Token과 Refresh Token을 발급받습니다."
    )
    @ApiCommonResponses.RefreshToken
    @ApiCommonResponses.TokenRelated
    @PostMapping("/refresh")
    public BaseResponse<RefreshTokenUseCase.TokenResponse> refreshToken(
            @Valid @RequestBody RefreshRequestDto requestDto
    ) {
        RefreshTokenUseCase.RefreshTokenCommand command = RefreshTokenUseCase.RefreshTokenCommand.builder()
                .refreshToken(requestDto.refreshToken)
                .build();
        return BaseResponse.success(refreshTokenUseCase.refreshToken(command));
    }

    @Data
    public static class RefreshRequestDto {
        @NotNull(message = "Refresh Token은 필수 입력값입니다.")
        private String refreshToken;
    }
}
