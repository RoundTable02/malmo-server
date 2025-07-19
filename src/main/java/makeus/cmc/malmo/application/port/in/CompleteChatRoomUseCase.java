package makeus.cmc.malmo.application.port.in;

import lombok.Builder;
import lombok.Data;

public interface CompleteChatRoomUseCase {
    void completeChatRoom(CompleteChatRoomCommand command);

    @Data
    @Builder
    class CompleteChatRoomCommand {
        private Long userId;
    }
}
