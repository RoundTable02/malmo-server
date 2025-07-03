package makeus.cmc.malmo.adaptor.in.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import makeus.cmc.malmo.adaptor.in.web.docs.ApiCommonResponses;
import makeus.cmc.malmo.adaptor.in.web.docs.SwaggerResponses;
import makeus.cmc.malmo.adaptor.in.web.dto.BaseResponse;
import makeus.cmc.malmo.application.port.in.CoupleLinkUseCase;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;

@Tag(name = "커플 관리 API", description = "커플 연결 및 관리 관련 API")
@RestController
@RequestMapping("/couples")
@RequiredArgsConstructor
public class CoupleController {

    private final CoupleLinkUseCase coupleLinkUseCase;

    @Operation(
            summary = "커플 연결",
            description = "커플 초대코드를 사용하여 커플을 연결합니다. JWT 토큰이 필요합니다.",
            security = @SecurityRequirement(name = "Bearer Authentication")
    )
    @ApiResponse(
            responseCode = "200",
            description = "커플 연결 성공",
            content = @Content(schema = @Schema(implementation = SwaggerResponses.CoupleLinkSuccessResponse.class))
    )
    @ApiCommonResponses.RequireAuth
    @PostMapping
    public BaseResponse<CoupleLinkUseCase.CoupleLinkResponse> linkCouple(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody CoupleLinkRequestDto requestDto
    ) {
        CoupleLinkUseCase.CoupleLinkCommand command = CoupleLinkUseCase.CoupleLinkCommand.builder()
                .coupleCode(requestDto.getCoupleCode())
                .userId(Long.valueOf(user.getUsername()))
                .build();
        return BaseResponse.success(coupleLinkUseCase.coupleLink(command));
    }

    @Operation(
            summary = "🚧 [개발 전] 커플 연결 끊기",
            description = "연결된 커플을 끊습니다. JWT 토큰이 필요합니다.",
            security = @SecurityRequirement(name = "Bearer Authentication")
    )
    @ApiResponse(
            responseCode = "200",
            description = "커플 연결 끊기 성공",
            content = @Content(schema = @Schema(implementation = SwaggerResponses.CoupleUnlinkSuccessResponse.class))
    )
    @ApiCommonResponses.RequireAuth
    @ApiCommonResponses.OnlyCouple
    @DeleteMapping
    public BaseResponse<CoupleUnlinkResponseDto> unlinkCouple(
            @AuthenticationPrincipal User user
    ) {
        return BaseResponse.success(CoupleUnlinkResponseDto.builder().build());
    }


    @Data
    public static class CoupleLinkRequestDto {
        @NotBlank(message = "초대코드는 필수 입력값입니다.")
        @Size(min = 6, max = 8, message = "커플 코드는 6 ~ 8자리여야 합니다.")
        private String coupleCode;
    }

    @Data
    @Builder
    public static class CoupleUnlinkResponseDto {
        private Long coupleId;
    }
}
