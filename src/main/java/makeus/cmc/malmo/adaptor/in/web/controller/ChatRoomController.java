package makeus.cmc.malmo.adaptor.in.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import makeus.cmc.malmo.adaptor.in.web.docs.ApiCommonResponses;
import makeus.cmc.malmo.adaptor.in.web.docs.SwaggerResponses;
import makeus.cmc.malmo.adaptor.in.web.dto.BaseListResponse;
import makeus.cmc.malmo.adaptor.in.web.dto.BaseResponse;
import makeus.cmc.malmo.application.port.in.GetChatRoomListUseCase;
import makeus.cmc.malmo.application.port.in.GetChatRoomMessagesUseCase;
import makeus.cmc.malmo.application.port.in.GetChatRoomSummaryUseCase;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;

@Tag(name = "채팅방 API", description = "사용자의 채팅방 관리 및 조회를 위한 API")
@RestController
@RequestMapping("/chatroom")
@RequiredArgsConstructor
public class ChatRoomController {

    private final GetChatRoomSummaryUseCase getChatRoomSummaryUseCase;
    private final GetChatRoomListUseCase getChatRoomListUseCase;
    private final GetChatRoomMessagesUseCase getChatRoomMessagesUseCase;

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
    public BaseResponse<BaseListResponse<GetChatRoomListUseCase.GetChatRoomResponse>> getCurrentChatRoom(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "keyword", defaultValue = "") String keyword,
            @AuthenticationPrincipal User user) {
        GetChatRoomListUseCase.GetChatRoomListCommand command = GetChatRoomListUseCase.GetChatRoomListCommand.builder()
                .userId(Long.valueOf(user.getUsername()))
                .keyword(keyword)
                .page(page)
                .size(size)
                .build();

        return BaseListResponse.success(getChatRoomListUseCase.getChatRoomList(command).getChatRoomList());
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
    @GetMapping("/{chatRoomId}/messages")
    public BaseResponse<BaseListResponse<GetChatRoomMessagesUseCase.ChatRoomMessageDto>> getCurrentChatRoomMessages(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @AuthenticationPrincipal User user, @PathVariable Long chatRoomId) {
        GetChatRoomMessagesUseCase.GetChatRoomMessagesCommand command = GetChatRoomMessagesUseCase.GetChatRoomMessagesCommand.builder()
                .userId(Long.valueOf(user.getUsername()))
                .chatRoomId(chatRoomId)
                .page(page)
                .size(size)
                .build();

        return BaseListResponse.success(getChatRoomMessagesUseCase.getChatRoomMessages(command).getMessages());
    }
}
