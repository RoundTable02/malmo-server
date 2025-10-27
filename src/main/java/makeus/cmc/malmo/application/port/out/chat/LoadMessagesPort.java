package makeus.cmc.malmo.application.port.out.chat;

import lombok.AllArgsConstructor;
import lombok.Data;
import makeus.cmc.malmo.domain.model.chat.ChatMessage;
import makeus.cmc.malmo.domain.value.id.ChatRoomId;
import makeus.cmc.malmo.domain.value.type.SenderType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

public interface LoadMessagesPort {
    Page<ChatRoomMessageRepositoryDto> loadMessagesDto(ChatRoomId chatRoomId, Pageable pageable);
    Page<ChatRoomMessageRepositoryDto> loadMessagesDtoAsc(ChatRoomId chatRoomId, Pageable pageable);
    List<ChatMessage> loadChatRoomMessagesByLevel(ChatRoomId chatRoomId, int level);

    List<ChatMessage> loadChatRoomLevelAndDetailedLevelMessages(ChatRoomId chatRoomId, int level, int detailedLevel);

    @Data
    @AllArgsConstructor
    class ChatRoomMessageRepositoryDto {
        private Long messageId;
        private SenderType senderType;
        private String content;
        private LocalDateTime createdAt;
        private boolean isSaved;
    }
}
