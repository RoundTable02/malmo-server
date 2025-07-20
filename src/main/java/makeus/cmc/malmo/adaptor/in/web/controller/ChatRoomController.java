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
import makeus.cmc.malmo.adaptor.in.web.dto.BaseResponse;
import makeus.cmc.malmo.application.port.in.GetChatRoomSummaryUseCase;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "채팅방 API", description = "사용자의 채팅방 관리 및 조회를 위한 API")
@RestController
@RequestMapping("/chatroom")
@RequiredArgsConstructor
public class ChatRoomController {

    private final GetChatRoomSummaryUseCase getChatRoomSummaryUseCase;

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
}
