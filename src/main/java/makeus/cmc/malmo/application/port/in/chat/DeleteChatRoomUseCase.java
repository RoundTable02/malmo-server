package makeus.cmc.malmo.application.port.in.chat;

import lombok.Builder;
import lombok.Data;

import java.util.List;

public interface DeleteChatRoomUseCase {

    void deleteChatRooms(DeleteChatRoomsCommand command);

    @Data
    @Builder
    class DeleteChatRoomsCommand {
        private Long userId;
        private List<Long> chatRoomIdList;
    }
}
