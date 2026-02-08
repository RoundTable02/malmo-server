package makeus.cmc.malmo.integration_test.dto_factory;

import java.util.List;

public class BookmarkRequestDtoFactory {

    public static CreateBookmarkRequest createBookmarkRequest(Long messageId) {
        return new CreateBookmarkRequest(messageId);
    }

    public static DeleteBookmarksRequest deleteBookmarksRequest(List<Long> bookmarkIds) {
        return new DeleteBookmarksRequest(bookmarkIds);
    }

    public record CreateBookmarkRequest(Long messageId) {}

    public record DeleteBookmarksRequest(List<Long> bookmarkIdList) {}
}
