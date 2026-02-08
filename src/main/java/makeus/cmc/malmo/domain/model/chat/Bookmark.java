package makeus.cmc.malmo.domain.model.chat;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import makeus.cmc.malmo.domain.value.id.ChatRoomId;
import makeus.cmc.malmo.domain.value.id.MemberId;
import makeus.cmc.malmo.domain.value.state.BookmarkState;

import java.time.LocalDateTime;

@Getter
@Builder(access = AccessLevel.PRIVATE)
public class Bookmark {
    private Long id;
    private ChatRoomId chatRoomId;
    private Long chatMessageId;
    private MemberId memberId;
    private BookmarkState bookmarkState;

    // BaseTimeEntity fields
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;
    private LocalDateTime deletedAt;

    public static Bookmark create(ChatRoomId chatRoomId, Long chatMessageId, MemberId memberId) {
        return Bookmark.builder()
                .chatRoomId(chatRoomId)
                .chatMessageId(chatMessageId)
                .memberId(memberId)
                .bookmarkState(BookmarkState.ALIVE)
                .build();
    }

    public static Bookmark from(Long id, ChatRoomId chatRoomId, Long chatMessageId,
                                 MemberId memberId, BookmarkState bookmarkState,
                                 LocalDateTime createdAt, LocalDateTime modifiedAt,
                                 LocalDateTime deletedAt) {
        return Bookmark.builder()
                .id(id)
                .chatRoomId(chatRoomId)
                .chatMessageId(chatMessageId)
                .memberId(memberId)
                .bookmarkState(bookmarkState)
                .createdAt(createdAt)
                .modifiedAt(modifiedAt)
                .deletedAt(deletedAt)
                .build();
    }

    public void softDelete() {
        this.bookmarkState = BookmarkState.DELETED;
        this.deletedAt = LocalDateTime.now();
    }
}
