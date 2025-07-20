package makeus.cmc.malmo.application.port.out;

import makeus.cmc.malmo.domain.model.chat.ChatRoom;
import makeus.cmc.malmo.domain.value.id.ChatRoomId;
import makeus.cmc.malmo.domain.value.id.MemberId;

import java.util.List;
import java.util.Optional;

public interface LoadChatRoomPort {
    Optional<ChatRoom> loadCurrentChatRoomByMemberId(MemberId memberId);
    Optional<ChatRoom> loadChatRoomById(ChatRoomId chatRoomId);
    Optional<ChatRoom> loadPausedChatRoomByMemberId(MemberId memberId);

    List<ChatRoom> loadAliveChatRoomsByMemberId(MemberId memberId, String keyword, int page, int size);
}
