package makeus.cmc.malmo.adaptor.in.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import makeus.cmc.malmo.adaptor.in.web.docs.ApiCommonResponses;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;

@Tag(name = "테스트 API", description = "개발 및 테스트용 API, 공통 응답을 지원하지 않습니다.")
@Slf4j
@RestController
@RequiredArgsConstructor
public class TestController {

    @Operation(
            summary = "API 연결 테스트",
            description = "서버 연결 상태를 확인하는 테스트 API입니다. 인증이 필요하지 않습니다."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "요청 성공",
                    content = @Content(
                            mediaType = "text/plain",
                            schema = @Schema(type = "string", example = "API Test Success")
                    )
            )
    })
    @GetMapping("/test")
    public String test() {
        return "API Test Success";
    }

    @Operation(
            summary = "내 정보 조회",
            description = "현재 로그인한 사용자의 정보를 조회합니다. JWT 토큰이 필요합니다.",
            security = @SecurityRequirement(name = "Bearer Authentication")
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "요청 성공",
                    content = @Content(
                            mediaType = "text/plain",
                            schema = @Schema(type = "string", example = "My Member ID is 1")
                    )
            )
    })
    @ApiCommonResponses.RequireAuth
    @GetMapping("/me")
    public String getMyInfo(@AuthenticationPrincipal User user) {
        log.info("Current User ID: {}", user.getUsername());
        log.info("Current User Authorities: {}", user.getAuthorities());
        return "My Member ID is " + user.getUsername();
    }

    @Operation(
            summary = "관리자 테스트",
            description = "관리자 권한이 필요한 테스트 API입니다.",
            security = @SecurityRequirement(name = "Bearer Authentication")
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "요청 성공",
                    content = @Content(
                            mediaType = "text/plain",
                            schema = @Schema(type = "string", example = "Admin Test Success")
                    )
            )
    })
    @ApiCommonResponses.RequireAdmin
    @GetMapping("/admin/test")
    public String adminTest() {
        return "Admin Test Success";
    }
}
