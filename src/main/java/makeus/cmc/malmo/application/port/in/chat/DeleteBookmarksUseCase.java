package makeus.cmc.malmo.application.port.in.chat;

import lombok.Builder;
import lombok.Data;

import java.util.List;

public interface DeleteBookmarksUseCase {

    void deleteBookmarks(DeleteBookmarksCommand command);

    @Data
    @Builder
    class DeleteBookmarksCommand {
        private Long userId;
        private Long chatRoomId;
        private List<Long> bookmarkIdList;
    }
}
