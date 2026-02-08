package makeus.cmc.malmo.application.port.in.chat;

import lombok.Builder;
import lombok.Data;

public interface SendChatMessageUseCase {
    SendChatMessageResponse processUserMessage(SendChatMessageCommand command);

//    void upgradeChatRoom(SendChatMessageCommand command);

    @Data
    @Builder
    class SendChatMessageCommand {
        private Long userId;
        private Long chatRoomId;  // 명시적으로 채팅방 ID 지정
        private String message;
    }

    @Data
    @Builder
    class SendChatMessageResponse {
        private Long messageId;
    }
}
