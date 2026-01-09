package makeus.cmc.malmo.application.port.out.chat;

import lombok.AllArgsConstructor;
import lombok.Data;
import makeus.cmc.malmo.domain.model.chat.Bookmark;
import makeus.cmc.malmo.domain.value.id.BookmarkId;
import makeus.cmc.malmo.domain.value.id.ChatRoomId;
import makeus.cmc.malmo.domain.value.id.MemberId;
import makeus.cmc.malmo.domain.value.type.SenderType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface LoadBookmarkPort {

    Optional<Bookmark> loadBookmarkById(BookmarkId bookmarkId);

    Optional<Bookmark> loadBookmarkByMemberAndMessage(MemberId memberId, Long chatMessageId);

    Page<BookmarkDto> loadBookmarksByMemberAndChatRoom(MemberId memberId, ChatRoomId chatRoomId, Pageable pageable);

    boolean existsByMemberAndMessage(MemberId memberId, Long chatMessageId);

    boolean isMemberOwnerOfBookmarks(MemberId memberId, List<BookmarkId> bookmarkIds);

    long countMessagesBeforeId(ChatRoomId chatRoomId, Long messageId, String sort);

    @Data
    @AllArgsConstructor
    class BookmarkDto {
        private Long bookmarkId;
        private Long chatMessageId;
        private String content;
        private SenderType senderType;
        private LocalDateTime createdAt;
    }
}
