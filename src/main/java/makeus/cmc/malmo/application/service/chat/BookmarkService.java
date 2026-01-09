package makeus.cmc.malmo.application.service.chat;

import lombok.RequiredArgsConstructor;
import makeus.cmc.malmo.adaptor.in.aop.CheckValidMember;
import makeus.cmc.malmo.application.exception.BookmarkAlreadyExistsException;
import makeus.cmc.malmo.application.exception.MemberAccessDeniedException;
import makeus.cmc.malmo.application.helper.chat_room.BookmarkCommandHelper;
import makeus.cmc.malmo.application.helper.chat_room.BookmarkQueryHelper;
import makeus.cmc.malmo.application.helper.chat_room.ChatRoomQueryHelper;
import makeus.cmc.malmo.application.port.in.chat.CreateBookmarkUseCase;
import makeus.cmc.malmo.application.port.in.chat.DeleteBookmarksUseCase;
import makeus.cmc.malmo.application.port.in.chat.GetBookmarkListUseCase;
import makeus.cmc.malmo.application.port.in.chat.GetMessagesByBookmarkUseCase;
import makeus.cmc.malmo.application.port.out.chat.LoadBookmarkPort;
import makeus.cmc.malmo.application.port.out.chat.LoadMessagesPort;
import makeus.cmc.malmo.domain.model.chat.Bookmark;
import makeus.cmc.malmo.domain.model.chat.ChatMessage;
import makeus.cmc.malmo.domain.value.id.BookmarkId;
import makeus.cmc.malmo.domain.value.id.ChatRoomId;
import makeus.cmc.malmo.domain.value.id.MemberId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BookmarkService implements CreateBookmarkUseCase, DeleteBookmarksUseCase,
        GetBookmarkListUseCase, GetMessagesByBookmarkUseCase {

    private final BookmarkQueryHelper bookmarkQueryHelper;
    private final BookmarkCommandHelper bookmarkCommandHelper;
    private final ChatRoomQueryHelper chatRoomQueryHelper;

    @Override
    @CheckValidMember
    @Transactional
    public CreateBookmarkResponse createBookmark(CreateBookmarkCommand command) {
        MemberId memberId = MemberId.of(command.getUserId());
        ChatRoomId chatRoomId = ChatRoomId.of(command.getChatRoomId());

        // 1. 채팅방 소유권 검증
        chatRoomQueryHelper.validateChatRoomOwnership(memberId, chatRoomId);

        // 2. 이미 북마크가 존재하는지 확인
        if (bookmarkQueryHelper.existsByMemberAndMessage(memberId, command.getMessageId())) {
            throw new BookmarkAlreadyExistsException();
        }

        // 3. 메시지가 해당 채팅방에 존재하는지 검증
        ChatMessage message = bookmarkQueryHelper.validateMessageInChatRoom(
                command.getMessageId(), chatRoomId);

        // 4. 북마크 생성 및 저장
        Bookmark bookmark = Bookmark.create(chatRoomId, command.getMessageId(), memberId);
        Bookmark savedBookmark = bookmarkCommandHelper.saveBookmark(bookmark);

        return CreateBookmarkResponse.builder()
                .bookmarkId(savedBookmark.getId())
                .content(message.getContent())
                .type(message.getSenderType())
                .timestamp(message.getCreatedAt())
                .build();
    }

    @Override
    @CheckValidMember
    @Transactional
    public void deleteBookmarks(DeleteBookmarksCommand command) {
        MemberId memberId = MemberId.of(command.getUserId());
        ChatRoomId chatRoomId = ChatRoomId.of(command.getChatRoomId());

        // 1. 채팅방 소유권 검증
        chatRoomQueryHelper.validateChatRoomOwnership(memberId, chatRoomId);

        // 2. 북마크 소유권 검증
        List<BookmarkId> bookmarkIds = command.getBookmarkIdList().stream()
                .map(BookmarkId::of)
                .toList();
        bookmarkQueryHelper.validateBookmarksOwnership(memberId, bookmarkIds);

        // 3. 북마크 soft delete
        bookmarkCommandHelper.softDeleteBookmarks(bookmarkIds);
    }

    @Override
    @CheckValidMember
    public GetBookmarkListResponse getBookmarkList(GetBookmarkListCommand command) {
        MemberId memberId = MemberId.of(command.getUserId());
        ChatRoomId chatRoomId = ChatRoomId.of(command.getChatRoomId());

        // 1. 채팅방 소유권 검증
        chatRoomQueryHelper.validateChatRoomOwnership(memberId, chatRoomId);

        // 2. 페이지네이션된 북마크 조회
        Page<LoadBookmarkPort.BookmarkDto> bookmarks = bookmarkQueryHelper.getBookmarksByMemberAndChatRoom(
                memberId, chatRoomId, command.getPageable());

        List<BookmarkDto> dtos = bookmarks.getContent().stream()
                .map(b -> BookmarkDto.builder()
                        .bookmarkId(b.getBookmarkId())
                        .content(b.getContent())
                        .type(b.getSenderType())
                        .timestamp(b.getCreatedAt())
                        .build())
                .toList();

        return GetBookmarkListResponse.builder()
                .bookmarkList(dtos)
                .totalCount(bookmarks.getTotalElements())
                .build();
    }

    @Override
    @CheckValidMember
    public GetMessagesByBookmarkResponse getMessagesByBookmark(GetMessagesByBookmarkCommand command) {
        MemberId memberId = MemberId.of(command.getUserId());

        // 1. 북마크 조회 및 검증
        Bookmark bookmark = bookmarkQueryHelper.getBookmarkByIdOrThrow(
                BookmarkId.of(command.getBookmarkId()));

        // 2. 북마크 소유권 검증
        if (!bookmark.getMemberId().getValue().equals(command.getUserId())) {
            throw new MemberAccessDeniedException("북마크에 접근할 권한이 없습니다.");
        }

        // 3. 북마크된 메시지가 포함된 페이지 계산
        int page = bookmarkQueryHelper.calculatePageForMessage(
                bookmark.getChatRoomId(),
                bookmark.getChatMessageId(),
                command.getSize(),
                command.getSort());

        // 4. 해당 페이지의 메시지 조회
        Sort sort = "ASC".equalsIgnoreCase(command.getSort())
                ? Sort.by("createdAt").ascending()
                : Sort.by("createdAt").descending();
        Pageable pageable = PageRequest.of(page, command.getSize(), sort);

        Page<LoadMessagesPort.ChatRoomMessageRepositoryDto> messages =
                "ASC".equalsIgnoreCase(command.getSort())
                        ? chatRoomQueryHelper.getChatMessagesDtoAsc(bookmark.getChatRoomId(), memberId, pageable)
                        : chatRoomQueryHelper.getChatMessagesDtoDesc(bookmark.getChatRoomId(), memberId, pageable);

        List<MessageDto> messageDtos = messages.getContent().stream()
                .map(m -> MessageDto.builder()
                        .messageId(m.getMessageId())
                        .content(m.getContent())
                        .senderType(m.getSenderType())
                        .createdAt(m.getCreatedAt())
                        .isSaved(m.isSaved())
                        .build())
                .toList();

        return GetMessagesByBookmarkResponse.builder()
                .targetMessageId(bookmark.getChatMessageId())
                .size(command.getSize())
                .page(page)
                .messages(messageDtos)
                .build();
    }
}
