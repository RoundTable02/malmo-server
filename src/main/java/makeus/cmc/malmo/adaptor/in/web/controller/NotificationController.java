package makeus.cmc.malmo.adaptor.in.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import makeus.cmc.malmo.adaptor.in.web.docs.ApiCommonResponses;
import makeus.cmc.malmo.adaptor.in.web.docs.SwaggerResponses;
import makeus.cmc.malmo.adaptor.in.web.dto.BaseResponse;
import makeus.cmc.malmo.application.port.in.notification.GetPendingNotificationUseCase;
import makeus.cmc.malmo.application.port.in.notification.ProcessReadNotificationsUseCase;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "멤버 알람 관리 API", description = "Member 미조회 알람 관리용 API")
@Slf4j
@RestController
@RequestMapping("/members/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final GetPendingNotificationUseCase getPendingNotificationUseCase;
    private final ProcessReadNotificationsUseCase processReadNotificationsUseCase;

    @Operation(
            summary = "멤버 미조회 알림 조회",
            description = "현재 로그인된 멤버의 미조회 알림을 조회합니다. JWT 토큰이 필요합니다.",
            security = @SecurityRequirement(name = "Bearer Authentication")
    )
    @ApiResponse(
            responseCode = "200",
            description = "멤버 알림 정보 조회 성공",
            content = @Content(schema = @Schema(implementation = SwaggerResponses.GetPendingMemberNotificationSuccessResponse.class))
    )
    @ApiCommonResponses.RequireAuth
    @GetMapping("/pending")
    public BaseResponse<GetPendingNotificationUseCase.GetPendingNotificationResponse> getMemberPendingNotification(
            @AuthenticationPrincipal User user
    ) {
        GetPendingNotificationUseCase.GetPendingNotificationCommand command = GetPendingNotificationUseCase.GetPendingNotificationCommand.builder()
                .userId(Long.valueOf(user.getUsername()))
                .build();
        return BaseResponse.success(getPendingNotificationUseCase.getPendingNotifications(command));
    }

    @Operation(
            summary = "멤버 미조회 알림 조회 처리",
            description = "현재 로그인된 멤버의 미조회 알림을 조회 처리합니다. JWT 토큰이 필요합니다.",
            security = @SecurityRequirement(name = "Bearer Authentication")
    )
    @ApiResponse(
            responseCode = "200",
            description = "멤버 알림 정보 조회 처리 성공",
            content = @Content(schema = @Schema(implementation = SwaggerResponses.ReadMemberNotificationSuccessResponse.class))
    )
    @ApiCommonResponses.RequireAuth
    @PatchMapping("/pending")
    public BaseResponse processNotifications(
            @AuthenticationPrincipal User user,
            @RequestBody ProcessNotificationsRequestDto requestDto
    ) {
        ProcessReadNotificationsUseCase.ProcessReadNotificationsCommand command = ProcessReadNotificationsUseCase.ProcessReadNotificationsCommand.builder()
                .userId(Long.valueOf(user.getUsername()))
                .notificationIds(requestDto.getPendingNotifications())
                .build();

        processReadNotificationsUseCase.processReadNotifications(command);

        return BaseResponse.success(null);
    }

    @Data
    public static class ProcessNotificationsRequestDto {
        private List<Long> pendingNotifications;
    }
}
