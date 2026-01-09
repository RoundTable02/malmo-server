package makeus.cmc.malmo.application.helper.chat_room;

import lombok.RequiredArgsConstructor;
import makeus.cmc.malmo.application.exception.BookmarkNotFoundException;
import makeus.cmc.malmo.application.exception.MemberAccessDeniedException;
import makeus.cmc.malmo.application.exception.MessageNotFoundException;
import makeus.cmc.malmo.application.port.out.chat.LoadBookmarkPort;
import makeus.cmc.malmo.application.port.out.chat.LoadMessagesPort;
import makeus.cmc.malmo.domain.model.chat.Bookmark;
import makeus.cmc.malmo.domain.model.chat.ChatMessage;
import makeus.cmc.malmo.domain.value.id.BookmarkId;
import makeus.cmc.malmo.domain.value.id.ChatRoomId;
import makeus.cmc.malmo.domain.value.id.MemberId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class BookmarkQueryHelper {

    private final LoadBookmarkPort loadBookmarkPort;
    private final LoadMessagesPort loadMessagesPort;

    public Bookmark getBookmarkByIdOrThrow(BookmarkId bookmarkId) {
        return loadBookmarkPort.loadBookmarkById(bookmarkId)
                .orElseThrow(BookmarkNotFoundException::new);
    }

    public boolean existsByMemberAndMessage(MemberId memberId, Long chatMessageId) {
        return loadBookmarkPort.existsByMemberAndMessage(memberId, chatMessageId);
    }

    public Page<LoadBookmarkPort.BookmarkDto> getBookmarksByMemberAndChatRoom(
            MemberId memberId, ChatRoomId chatRoomId, Pageable pageable) {
        return loadBookmarkPort.loadBookmarksByMemberAndChatRoom(memberId, chatRoomId, pageable);
    }

    public void validateBookmarksOwnership(MemberId memberId, List<BookmarkId> bookmarkIds) {
        boolean valid = loadBookmarkPort.isMemberOwnerOfBookmarks(memberId, bookmarkIds);
        if (!valid) {
            throw new MemberAccessDeniedException("북마크에 접근할 권한이 없습니다.");
        }
    }

    public int calculatePageForMessage(ChatRoomId chatRoomId, Long messageId, int pageSize, String sort) {
        long position = loadBookmarkPort.countMessagesBeforeId(chatRoomId, messageId, sort);
        return (int) (position / pageSize);
    }

    public ChatMessage getMessageByIdOrThrow(Long messageId) {
        return loadMessagesPort.loadMessageById(messageId)
                .orElseThrow(MessageNotFoundException::new);
    }

    public ChatMessage validateMessageInChatRoom(Long messageId, ChatRoomId chatRoomId) {
        ChatMessage message = loadMessagesPort.loadMessageById(messageId)
                .orElseThrow(MessageNotFoundException::new);

        if (!message.getChatRoomId().getValue().equals(chatRoomId.getValue())) {
            throw new MessageNotFoundException("해당 채팅방에 존재하지 않는 메시지입니다.");
        }

        return message;
    }
}
