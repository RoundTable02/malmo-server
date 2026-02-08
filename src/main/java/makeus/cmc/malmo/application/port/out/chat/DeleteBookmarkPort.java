package makeus.cmc.malmo.application.port.out.chat;

import makeus.cmc.malmo.domain.value.id.BookmarkId;

import java.util.List;

public interface DeleteBookmarkPort {

    void softDeleteBookmarks(List<BookmarkId> bookmarkIds);
}
