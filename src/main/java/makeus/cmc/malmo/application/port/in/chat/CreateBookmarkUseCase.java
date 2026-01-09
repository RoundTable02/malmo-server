package makeus.cmc.malmo.application.port.in.chat;

import lombok.Builder;
import lombok.Data;
import makeus.cmc.malmo.domain.value.type.SenderType;

import java.time.LocalDateTime;

public interface CreateBookmarkUseCase {

    CreateBookmarkResponse createBookmark(CreateBookmarkCommand command);

    @Data
    @Builder
    class CreateBookmarkCommand {
        private Long userId;
        private Long chatRoomId;
        private Long messageId;
    }

    @Data
    @Builder
    class CreateBookmarkResponse {
        private Long bookmarkId;
        private String content;
        private SenderType type;
        private LocalDateTime timestamp;
    }
}
