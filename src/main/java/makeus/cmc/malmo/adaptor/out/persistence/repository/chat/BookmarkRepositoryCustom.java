package makeus.cmc.malmo.adaptor.out.persistence.repository.chat;

import makeus.cmc.malmo.application.port.out.chat.LoadBookmarkPort;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface BookmarkRepositoryCustom {

    Page<LoadBookmarkPort.BookmarkDto> loadBookmarksByMemberAndChatRoom(
            Long memberId, Long chatRoomId, Pageable pageable);

    boolean isMemberOwnerOfBookmarks(Long memberId, List<Long> bookmarkIds);

    void softDeleteBookmarks(List<Long> bookmarkIds);

    long countMessagesBeforeId(Long chatRoomId, Long messageId, String sort);
}
