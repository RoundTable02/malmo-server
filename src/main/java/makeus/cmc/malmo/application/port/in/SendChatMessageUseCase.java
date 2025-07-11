package makeus.cmc.malmo.application.port.in;

import lombok.Builder;
import lombok.Data;

public interface SendChatMessageUseCase {
    void processUserMessage(SendChatMessageCommand command);

    @Data
    @Builder
    class SendChatMessageCommand {
        private Long userId;
        private String message;
    }
}
