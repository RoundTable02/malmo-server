package makeus.cmc.malmo.application.port.in.chat;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import makeus.cmc.malmo.domain.value.type.SenderType;

import java.time.LocalDateTime;
import java.util.List;

public interface GetMessagesByBookmarkUseCase {

    GetMessagesByBookmarkResponse getMessagesByBookmark(GetMessagesByBookmarkCommand command);

    @Data
    @Builder
    class GetMessagesByBookmarkCommand {
        private Long userId;
        private Long bookmarkId;
        private int size;
        private String sort;
    }

    @Data
    @Builder
    class GetMessagesByBookmarkResponse {
        private Long targetMessageId;
        private int size;
        private int page;
        private List<MessageDto> messages;
    }

    @Data
    @Builder
    class MessageDto {
        private Long messageId;
        private String content;
        private SenderType senderType;
        private LocalDateTime createdAt;
        @JsonProperty("isSaved")
        private boolean isSaved;
    }
}
