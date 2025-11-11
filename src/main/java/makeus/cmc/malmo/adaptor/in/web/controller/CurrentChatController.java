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
import makeus.cmc.malmo.application.port.in.chat.CompleteChatRoomUseCase;
import makeus.cmc.malmo.application.port.in.chat.GetCurrentChatRoomMessagesUseCase;
import makeus.cmc.malmo.application.port.in.chat.GetCurrentChatRoomUseCase;
import makeus.cmc.malmo.application.port.in.chat.SendChatMessageUseCase;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;

@Tag(name = "채팅 API", description = "사용자 채팅 전송 및 현재 채팅방을 위한 API")
@RestController
@RequestMapping("/chatrooms/current")
@RequiredArgsConstructor
public class CurrentChatController {

    private final SendChatMessageUseCase sendChatMessageUseCase;
    private final GetCurrentChatRoomUseCase getCurrentChatRoomUseCase;
    private final GetCurrentChatRoomMessagesUseCase getCurrentChatRoomMessagesUseCase;
    private final CompleteChatRoomUseCase completeChatRoomUseCase;

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

    @Operation(
            summary = "현재 채팅방 메시지 조회",
            description = "현재 채팅방의 메시지를 페이지네이션으로 조회합니다. JWT 토큰이 필요합니다.",
            security = @SecurityRequirement(name = "Bearer Authentication")
    )
    @ApiResponse(
            responseCode = "200",
            description = "채팅방 상태 조회 성공",
            content = @Content(schema = @Schema(implementation = SwaggerResponses.ChatMessageListSuccessResponse.class))
    )
    @ApiCommonResponses.RequireAuth
    @GetMapping("/messages")
    public BaseResponse<BaseListResponse<GetCurrentChatRoomMessagesUseCase.ChatRoomMessageDto>> getCurrentChatRoomMessages(
            @PageableDefault(page = 0, size = 10) Pageable pageable,
            @AuthenticationPrincipal User user) {
        GetCurrentChatRoomMessagesUseCase.GetCurrentChatRoomMessagesCommand command = GetCurrentChatRoomMessagesUseCase.GetCurrentChatRoomMessagesCommand.builder()
                .userId(Long.valueOf(user.getUsername()))
                .pageable(pageable)
                .build();
        GetCurrentChatRoomMessagesUseCase.GetCurrentChatRoomMessagesResponse currentChatRoomMessages = getCurrentChatRoomMessagesUseCase.getCurrentChatRoomMessages(command);

        return BaseListResponse.success(currentChatRoomMessages.getMessages(), currentChatRoomMessages.getTotalCount());
    }

    @Operation(
            summary = "채팅 메시지 전송",
            description = "서버로 AI 상담을 위한 사용자의 메시지를 전달합니다. AI 응답은 SSE로 전달됩니다. JWT 토큰이 필요합니다.",
            security = @SecurityRequirement(name = "Bearer Authentication")
    )
    @ApiResponse(
            responseCode = "200",
            description = "메시지 전송 성공",
            content = @Content(schema = @Schema(implementation = SwaggerResponses.SendChatSuccessResponse.class))
    )
    @ApiCommonResponses.RequireAuth
    @PostMapping("/send")
    public BaseResponse<SendChatMessageUseCase.SendChatMessageResponse> sendChatMessage(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody ChatRequest request) {
        SendChatMessageUseCase.SendChatMessageResponse sendChatMessageResponse = sendChatMessageUseCase.processUserMessage(
                SendChatMessageUseCase.SendChatMessageCommand.builder()
                        .userId(Long.valueOf(user.getUsername()))
                        .message(request.getMessage())
                        .build());

        return BaseResponse.success(sendChatMessageResponse);
    }

    @Operation(
            summary = "채팅방 종료",
            description = "현재 채팅방을 종료합니다. JWT 토큰이 필요합니다.",
            security = @SecurityRequirement(name = "Bearer Authentication")
    )
    @ApiResponse(
            responseCode = "200",
            description = "채팅방 종료 성공; 데이터 응답 값은 없음",
            content = @Content(schema = @Schema(implementation = SwaggerResponses.CompleteChatRoomResponse.class))
    )
    @ApiCommonResponses.RequireAuth
    @PostMapping("/complete")
    public BaseResponse<CompleteChatRoomUseCase.CompleteChatRoomResponse> completeChatRoom(
            @AuthenticationPrincipal User user) {
        CompleteChatRoomUseCase.CompleteChatRoomCommand command = CompleteChatRoomUseCase.CompleteChatRoomCommand.builder()
                .userId(Long.valueOf(user.getUsername()))
                .build();

        return BaseResponse.success(completeChatRoomUseCase.completeChatRoom(command));
    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ChatRequest {
        @NotBlank(message = "메시지는 비어있을 수 없습니다.")
        private String message;
    }
}
