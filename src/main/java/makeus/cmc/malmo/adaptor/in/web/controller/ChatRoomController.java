package makeus.cmc.malmo.adaptor.in.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import makeus.cmc.malmo.adaptor.in.web.docs.ApiCommonResponses;
import makeus.cmc.malmo.adaptor.in.web.docs.SwaggerResponses;
import makeus.cmc.malmo.adaptor.in.web.dto.BaseListResponse;
import makeus.cmc.malmo.adaptor.in.web.dto.BaseResponse;
import makeus.cmc.malmo.application.port.in.CompleteChatRoomUseCase;
import makeus.cmc.malmo.application.port.in.GetCurrentChatRoomMessagesUseCase;
import makeus.cmc.malmo.application.port.in.GetCurrentChatRoomUseCase;
import makeus.cmc.malmo.application.port.in.SendChatMessageUseCase;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;

@Tag(name = "채팅 전송 API", description = "사용자 채팅 전송을 위한 API")
@RestController
@RequestMapping("/chat")
@RequiredArgsConstructor
public class ChatRoomController {

    private final GetCurrentChatRoomUseCase getCurrentChatRoomUseCase;

    @Operation(
            summary = "채팅방 상태 조회",
            description = "현재 채팅방의 상태를 조회합니다. JWT 토큰이 필요합니다.",
            security = @SecurityRequirement(name = "Bearer Authentication")
    )
    @ApiResponse(
            responseCode = "200",
            description = "채팅방 상태 조회 성공",
            content = @Content(schema = @Schema(implementation = SwaggerResponses.ChatRoomStateResponse.class))
    )
    @ApiCommonResponses.RequireAuth
    @GetMapping
    public BaseResponse<GetCurrentChatRoomUseCase.GetCurrentChatRoomResponse> getCurrentChatRoom(
            @AuthenticationPrincipal User user) {
        GetCurrentChatRoomUseCase.GetCurrentChatRoomCommand command = GetCurrentChatRoomUseCase.GetCurrentChatRoomCommand.builder()
                .userId(Long.valueOf(user.getUsername()))
                .build();
        return BaseResponse.success(getCurrentChatRoomUseCase.getCurrentChatRoom(command));
    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ChatRequest {
        @NotBlank(message = "메시지는 비어있을 수 없습니다.")
        private String message;
    }
}
