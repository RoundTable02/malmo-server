package makeus.cmc.malmo.application.port.out;

import makeus.cmc.malmo.domain.model.chat.ChatRoom;
import makeus.cmc.malmo.domain.model.value.MemberId;

import java.util.Optional;

public interface LoadChatRoomPort {
    Optional<ChatRoom> loadCurrentChatRoomByMemberId(MemberId memberId);
}
