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
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import makeus.cmc.malmo.adaptor.in.web.docs.ApiCommonResponses;
import makeus.cmc.malmo.adaptor.in.web.docs.SwaggerResponses;
import makeus.cmc.malmo.adaptor.in.web.dto.BaseListResponse;
import makeus.cmc.malmo.adaptor.in.web.dto.BaseResponse;
import makeus.cmc.malmo.application.port.in.chat.CreateChatRoomUseCase;
import makeus.cmc.malmo.application.port.in.chat.DeleteChatRoomUseCase;
import makeus.cmc.malmo.application.port.in.chat.GetChatRoomListUseCase;
import makeus.cmc.malmo.application.port.in.chat.GetChatRoomMessagesUseCase;
import makeus.cmc.malmo.application.port.in.chat.GetChatRoomSummaryUseCase;
import makeus.cmc.malmo.application.port.in.chat.SendChatMessageUseCase;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "채팅방 API", description = "사용자의 채팅방 관리 및 조회를 위한 API")
@RestController
@RequestMapping("/chatrooms")
@RequiredArgsConstructor
public class ChatRoomController {

    private final CreateChatRoomUseCase createChatRoomUseCase;
    private final GetChatRoomSummaryUseCase getChatRoomSummaryUseCase;
    private final GetChatRoomListUseCase getChatRoomListUseCase;
    private final GetChatRoomMessagesUseCase getChatRoomMessagesUseCase;
    private final DeleteChatRoomUseCase deleteChatRoomUseCase;
    private final SendChatMessageUseCase sendChatMessageUseCase;

    @Operation(
            summary = "채팅방 생성",
            description = "새로운 채팅방을 생성합니다. JWT 토큰이 필요합니다.",
            security = @SecurityRequirement(name = "Bearer Authentication")
    )
    @ApiResponse(
            responseCode = "200",
            description = "채팅방 생성 성공",
            content = @Content(schema = @Schema(implementation = SwaggerResponses.CreateChatRoomResponse.class))
    )
    @ApiCommonResponses.RequireAuth
    @PostMapping
    public BaseResponse<CreateChatRoomUseCase.CreateChatRoomResponse> createChatRoom(
            @AuthenticationPrincipal User user) {
        
        CreateChatRoomUseCase.CreateChatRoomResponse response = createChatRoomUseCase.createChatRoom(
                CreateChatRoomUseCase.CreateChatRoomCommand.builder()
                        .userId(Long.valueOf(user.getUsername()))
                        .build()
        );
        
        return BaseResponse.success(response);
    }

    @Operation(
            summary = "채팅방 요약 조회",
            description = "해당하는 채팅방의 요약를 조회합니다. JWT 토큰이 필요합니다.",
            security = @SecurityRequirement(name = "Bearer Authentication")
    )
    @ApiResponse(
            responseCode = "200",
            description = "채팅방 요약 조회 성공",
            content = @Content(schema = @Schema(implementation = SwaggerResponses.GetChatRoomSummaryResponse.class))
    )
    @ApiCommonResponses.RequireAuth
    @ApiCommonResponses.OnlyOwner
    @GetMapping("/{chatRoomId}/summary")
    public BaseResponse<GetChatRoomSummaryUseCase.GetChatRoomSummaryResponse> getCurrentChatRoom(
            @AuthenticationPrincipal User user, @PathVariable Long chatRoomId) {
        GetChatRoomSummaryUseCase.GetChatRoomSummaryCommand command = GetChatRoomSummaryUseCase.GetChatRoomSummaryCommand.builder()
                .userId(Long.valueOf(user.getUsername()))
                .chatRoomId(chatRoomId)
                .build();

        return BaseResponse.success(getChatRoomSummaryUseCase.getChatRoomSummary(command));
    }

    @Operation(
            summary = "채팅방 리스트 조회",
            description = "조건에 부합하는 채팅방 리스트를 조회합니다. JWT 토큰이 필요합니다.",
            security = @SecurityRequirement(name = "Bearer Authentication")
    )
    @ApiResponse(
            responseCode = "200",
            description = "채팅방 리스트 조회 성공",
            content = @Content(schema = @Schema(implementation = SwaggerResponses.ChatRoomListSuccessResponse.class))
    )
    @ApiCommonResponses.RequireAuth
    @GetMapping
    public BaseResponse<BaseListResponse<GetChatRoomListUseCase.GetChatRoomResponse>> getChatRoomList(
            @PageableDefault(page = 0, size = 10) Pageable pageable,
            @RequestParam(value = "keyword", defaultValue = "") String keyword,
            @AuthenticationPrincipal User user) {
        GetChatRoomListUseCase.GetChatRoomListCommand command = GetChatRoomListUseCase.GetChatRoomListCommand.builder()
                .userId(Long.valueOf(user.getUsername()))
                .keyword(keyword)
                .pageable(pageable)
                .build();

        GetChatRoomListUseCase.GetChatRoomListResponse response = getChatRoomListUseCase.getChatRoomList(command);

        return BaseListResponse.success(response.getChatRoomList(), response.getTotalCount());
    }

    @Operation(
            summary = "채팅방의 메시지 리스트 조회",
            description = "채팅방의 메시지를 페이지네이션으로 조회합니다. 현재 채팅방과 달리 시간 오름차순으로 전달됩니다. JWT 토큰이 필요합니다.",
            security = @SecurityRequirement(name = "Bearer Authentication")
    )
    @ApiResponse(
            responseCode = "200",
            description = "채팅방 메시지 리스트 조회 성공",
            content = @Content(schema = @Schema(implementation = SwaggerResponses.ChatMessageListSuccessResponse.class))
    )
    @ApiCommonResponses.RequireAuth
    @ApiCommonResponses.OnlyOwner
    @GetMapping("/{chatRoomId}/messages")
    public BaseResponse<BaseListResponse<GetChatRoomMessagesUseCase.ChatRoomMessageDto>> getChatRoomMessages(
            @PageableDefault(page = 0, size = 10) Pageable pageable,
            @AuthenticationPrincipal User user, @PathVariable Long chatRoomId) {
        GetChatRoomMessagesUseCase.GetChatRoomMessagesCommand command = GetChatRoomMessagesUseCase.GetChatRoomMessagesCommand.builder()
                .userId(Long.valueOf(user.getUsername()))
                .chatRoomId(chatRoomId)
                .pageable(pageable)
                .build();

        GetChatRoomMessagesUseCase.GetCurrentChatRoomMessagesResponse response = getChatRoomMessagesUseCase.getChatRoomMessages(command);
        return BaseListResponse.success(response.getMessages(), response.getTotalCount());
    }

    @Operation(
            summary = "채팅 메시지 전송",
            description = "특정 채팅방에 메시지를 전송합니다. AI 응답은 SSE로 전달됩니다. JWT 토큰이 필요합니다.",
            security = @SecurityRequirement(name = "Bearer Authentication")
    )
    @ApiResponse(
            responseCode = "200",
            description = "메시지 전송 성공",
            content = @Content(schema = @Schema(implementation = SwaggerResponses.SendChatSuccessResponse.class))
    )
    @ApiCommonResponses.RequireAuth
    @ApiCommonResponses.OnlyOwner
    @PostMapping("/{chatRoomId}/messages")
    public BaseResponse<SendChatMessageUseCase.SendChatMessageResponse> sendMessage(
            @AuthenticationPrincipal User user,
            @PathVariable Long chatRoomId,
            @Valid @RequestBody SendMessageRequest request) {
        
        SendChatMessageUseCase.SendChatMessageResponse response = sendChatMessageUseCase.processUserMessage(
                SendChatMessageUseCase.SendChatMessageCommand.builder()
                        .userId(Long.valueOf(user.getUsername()))
                        .chatRoomId(chatRoomId)
                        .message(request.getMessage())
                        .build()
        );
        
        return BaseResponse.success(response);
    }

    @Operation(
            summary = "채팅방 삭제",
            description = "채팅방을 id 리스트를 통해 다건 동시 삭제합니다. JWT 토큰이 필요합니다.",
            security = @SecurityRequirement(name = "Bearer Authentication")
    )
    @ApiResponse(
            responseCode = "200",
            description = "채팅방 삭제 성공",
            content = @Content(schema = @Schema(implementation = SwaggerResponses.ChatRoomDeleteSuccessResponse.class))
    )
    @ApiCommonResponses.RequireAuth
    @ApiCommonResponses.OnlyOwner
    @DeleteMapping
    public BaseResponse<Void> deleteChatRooms(
            @AuthenticationPrincipal User user, @RequestBody DeleteChatRoomRequestDto requestDto) {
        DeleteChatRoomUseCase.DeleteChatRoomsCommand command = DeleteChatRoomUseCase.DeleteChatRoomsCommand.builder()
                .userId(Long.valueOf(user.getUsername()))
                .chatRoomIdList(requestDto.chatRoomIdList)
                .build();
        deleteChatRoomUseCase.deleteChatRooms(command);

        return BaseResponse.success(null);
    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class SendMessageRequest {
        @NotBlank(message = "메시지는 비어있을 수 없습니다.")
        private String message;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class DeleteChatRoomRequestDto {
        private List<Long> chatRoomIdList;
    }
}
