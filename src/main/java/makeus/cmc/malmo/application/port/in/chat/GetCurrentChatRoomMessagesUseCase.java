package makeus.cmc.malmo.application.port.in.chat;

import lombok.Builder;
import lombok.Data;
import makeus.cmc.malmo.domain.value.type.SenderType;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

public interface GetCurrentChatRoomMessagesUseCase {

    GetCurrentChatRoomMessagesResponse getCurrentChatRoomMessages(GetCurrentChatRoomMessagesCommand command);

    @Data
    @Builder
    class GetCurrentChatRoomMessagesCommand {
        private Long userId;
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
        private Long bookmarkId;
    }
}
