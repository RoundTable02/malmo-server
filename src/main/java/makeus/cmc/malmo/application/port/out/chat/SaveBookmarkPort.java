package makeus.cmc.malmo.application.port.out.chat;

import makeus.cmc.malmo.domain.model.chat.Bookmark;

public interface SaveBookmarkPort {

    Bookmark saveBookmark(Bookmark bookmark);
}
