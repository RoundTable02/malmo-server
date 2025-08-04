package makeus.cmc.malmo.application.port.in.chat;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import makeus.cmc.malmo.domain.value.type.SenderType;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

public interface GetChatRoomMessagesUseCase {

    GetCurrentChatRoomMessagesResponse getChatRoomMessages(GetChatRoomMessagesCommand command);

    @Data
    @Builder
    class GetChatRoomMessagesCommand {
        private Long userId;
        private Long chatRoomId;
        private Pageable pageable;
    }

    @Data
    @Builder
    class GetCurrentChatRoomMessagesResponse {
        private List<ChatRoomMessageDto> messages;
        private Long totalCount;
    }

    @Data
    @Builder
    class ChatRoomMessageDto {
        private Long messageId;
        private SenderType senderType;
        private String content;
        private LocalDateTime createdAt;
        @JsonProperty("isSaved")
        private boolean isSaved;
    }
}
