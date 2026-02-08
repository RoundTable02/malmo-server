package makeus.cmc.malmo.application.helper.chat_room;

import lombok.RequiredArgsConstructor;
import makeus.cmc.malmo.application.port.out.chat.DeleteBookmarkPort;
import makeus.cmc.malmo.application.port.out.chat.SaveBookmarkPort;
import makeus.cmc.malmo.domain.model.chat.Bookmark;
import makeus.cmc.malmo.domain.value.id.BookmarkId;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class BookmarkCommandHelper {

    private final SaveBookmarkPort saveBookmarkPort;
    private final DeleteBookmarkPort deleteBookmarkPort;

    public Bookmark saveBookmark(Bookmark bookmark) {
        return saveBookmarkPort.saveBookmark(bookmark);
    }

    public void softDeleteBookmarks(List<BookmarkId> bookmarkIds) {
        deleteBookmarkPort.softDeleteBookmarks(bookmarkIds);
    }
}
