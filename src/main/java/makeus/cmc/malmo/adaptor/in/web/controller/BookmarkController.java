package makeus.cmc.malmo.adaptor.in.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import makeus.cmc.malmo.adaptor.in.web.docs.ApiCommonResponses;
import makeus.cmc.malmo.adaptor.in.web.dto.BaseListResponse;
import makeus.cmc.malmo.adaptor.in.web.dto.BaseResponse;
import makeus.cmc.malmo.application.port.in.chat.CreateBookmarkUseCase;
import makeus.cmc.malmo.application.port.in.chat.DeleteBookmarksUseCase;
import makeus.cmc.malmo.application.port.in.chat.GetBookmarkListUseCase;
import makeus.cmc.malmo.application.port.in.chat.GetMessagesByBookmarkUseCase;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "북마크 API", description = "채팅 메시지 북마크 관리를 위한 API")
@RestController
@RequestMapping("/chatrooms/{chatRoomId}/bookmarks")
@RequiredArgsConstructor
public class BookmarkController {

    private final CreateBookmarkUseCase createBookmarkUseCase;
    private final DeleteBookmarksUseCase deleteBookmarksUseCase;
    private final GetBookmarkListUseCase getBookmarkListUseCase;
    private final GetMessagesByBookmarkUseCase getMessagesByBookmarkUseCase;

    @Operation(
            summary = "북마크 생성",
            description = "채팅 메시지를 북마크합니다. JWT 토큰이 필요합니다.",
            security = @SecurityRequirement(name = "Bearer Authentication")
    )
    @ApiResponse(
            responseCode = "200",
            description = "북마크 생성 성공"
    )
    @ApiCommonResponses.RequireAuth
    @PostMapping
    public BaseResponse<CreateBookmarkUseCase.CreateBookmarkResponse> createBookmark(
            @AuthenticationPrincipal User user,
            @PathVariable Long chatRoomId,
            @RequestBody CreateBookmarkRequestDto requestDto) {

        CreateBookmarkUseCase.CreateBookmarkCommand command =
                CreateBookmarkUseCase.CreateBookmarkCommand.builder()
                        .userId(Long.valueOf(user.getUsername()))
                        .chatRoomId(chatRoomId)
                        .messageId(requestDto.getMessageId())
                        .build();

        return BaseResponse.success(createBookmarkUseCase.createBookmark(command));
    }

    @Operation(
            summary = "북마크 삭제 (다건)",
            description = "북마크를 삭제합니다 (소프트 삭제). JWT 토큰이 필요합니다.",
            security = @SecurityRequirement(name = "Bearer Authentication")
    )
    @ApiResponse(
            responseCode = "200",
            description = "북마크 삭제 성공"
    )
    @ApiCommonResponses.RequireAuth
    @DeleteMapping
    public BaseResponse<Void> deleteBookmarks(
            @AuthenticationPrincipal User user,
            @PathVariable Long chatRoomId,
            @RequestBody DeleteBookmarksRequestDto requestDto) {

        DeleteBookmarksUseCase.DeleteBookmarksCommand command =
                DeleteBookmarksUseCase.DeleteBookmarksCommand.builder()
                        .userId(Long.valueOf(user.getUsername()))
                        .chatRoomId(chatRoomId)
                        .bookmarkIdList(requestDto.getBookmarkIdList())
                        .build();

        deleteBookmarksUseCase.deleteBookmarks(command);
        return BaseResponse.success(null);
    }

    @Operation(
            summary = "북마크 리스트 조회",
            description = "채팅방의 북마크 목록을 조회합니다. JWT 토큰이 필요합니다.",
            security = @SecurityRequirement(name = "Bearer Authentication")
    )
    @ApiResponse(
            responseCode = "200",
            description = "북마크 리스트 조회 성공"
    )
    @ApiCommonResponses.RequireAuth
    @GetMapping
    public BaseResponse<BaseListResponse<GetBookmarkListUseCase.BookmarkDto>> getBookmarkList(
            @AuthenticationPrincipal User user,
            @PathVariable Long chatRoomId,
            @PageableDefault(page = 0, size = 10) Pageable pageable) {

        GetBookmarkListUseCase.GetBookmarkListCommand command =
                GetBookmarkListUseCase.GetBookmarkListCommand.builder()
                        .userId(Long.valueOf(user.getUsername()))
                        .chatRoomId(chatRoomId)
                        .pageable(pageable)
                        .build();

        GetBookmarkListUseCase.GetBookmarkListResponse response =
                getBookmarkListUseCase.getBookmarkList(command);

        return BaseListResponse.success(response.getBookmarkList(), response.getTotalCount());
    }

    @Operation(
            summary = "북마크 기반 메시지 조회",
            description = "북마크된 메시지 위치로 이동하기 위한 메시지 목록을 조회합니다. JWT 토큰이 필요합니다.",
            security = @SecurityRequirement(name = "Bearer Authentication")
    )
    @ApiResponse(
            responseCode = "200",
            description = "북마크 기반 메시지 조회 성공"
    )
    @ApiCommonResponses.RequireAuth
    @GetMapping("/{bookmarkId}/messages")
    public BaseResponse<GetMessagesByBookmarkUseCase.GetMessagesByBookmarkResponse> getMessagesByBookmark(
            @AuthenticationPrincipal User user,
            @PathVariable Long chatRoomId,
            @PathVariable Long bookmarkId,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "ASC") String sort) {

        GetMessagesByBookmarkUseCase.GetMessagesByBookmarkCommand command =
                GetMessagesByBookmarkUseCase.GetMessagesByBookmarkCommand.builder()
                        .userId(Long.valueOf(user.getUsername()))
                        .bookmarkId(bookmarkId)
                        .size(size)
                        .sort(sort)
                        .build();

        return BaseResponse.success(getMessagesByBookmarkUseCase.getMessagesByBookmark(command));
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreateBookmarkRequestDto {
        private Long messageId;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DeleteBookmarksRequestDto {
        private List<Long> bookmarkIdList;
    }
}
