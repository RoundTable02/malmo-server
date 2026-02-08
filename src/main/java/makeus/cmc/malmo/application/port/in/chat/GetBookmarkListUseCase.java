package makeus.cmc.malmo.application.port.in.chat;

import lombok.Builder;
import lombok.Data;
import makeus.cmc.malmo.domain.value.type.SenderType;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

public interface GetBookmarkListUseCase {

    GetBookmarkListResponse getBookmarkList(GetBookmarkListCommand command);

    @Data
    @Builder
    class GetBookmarkListCommand {
        private Long userId;
        private Long chatRoomId;
        private Pageable pageable;
    }

    @Data
    @Builder
    class GetBookmarkListResponse {
        private List<BookmarkDto> bookmarkList;
        private Long totalCount;
    }

    @Data
    @Builder
    class BookmarkDto {
        private Long bookmarkId;
        private String content;
        private SenderType type;
        private LocalDateTime timestamp;
    }
}
