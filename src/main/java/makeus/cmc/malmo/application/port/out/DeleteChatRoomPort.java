package makeus.cmc.malmo.application.port.out;

import makeus.cmc.malmo.domain.value.id.ChatRoomId;
import makeus.cmc.malmo.domain.value.id.MemberId;

import java.util.List;

public interface DeleteChatRoomPort {
    void deleteChatRooms(List<ChatRoomId> chatRoomIds);
}
