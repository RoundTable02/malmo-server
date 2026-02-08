package makeus.cmc.malmo.application.port.in.chat;

import lombok.Builder;
import lombok.Getter;
import makeus.cmc.malmo.domain.value.state.ChatRoomState;

import java.time.LocalDateTime;

public interface CreateChatRoomUseCase {
    
    CreateChatRoomResponse createChatRoom(CreateChatRoomCommand command);

    @Builder
    @Getter
    class CreateChatRoomCommand {
        private final Long userId;
    }

    @Builder
    @Getter
    class CreateChatRoomResponse {
        private final Long chatRoomId;
        private final ChatRoomState chatRoomState;
        private final LocalDateTime createdAt;
    }
}
