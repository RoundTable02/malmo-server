package makeus.cmc.malmo.application.port.in;

import lombok.Builder;
import lombok.Data;

public interface CompleteChatRoomUseCase {
    CompleteChatRoomResponse completeChatRoom(CompleteChatRoomCommand command);

    @Data
    @Builder
    class CompleteChatRoomCommand {
        private Long userId;
    }

    @Data
    @Builder
    class CompleteChatRoomResponse {
        private Long chatRoomId;
    }
}
