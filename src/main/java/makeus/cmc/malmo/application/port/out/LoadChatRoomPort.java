package makeus.cmc.malmo.application.port.out;

import makeus.cmc.malmo.domain.model.chat.ChatRoom;
import makeus.cmc.malmo.domain.value.id.ChatRoomId;
import makeus.cmc.malmo.domain.value.id.MemberId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface LoadChatRoomPort {
    Optional<ChatRoom> loadCurrentChatRoomByMemberId(MemberId memberId);
    Optional<ChatRoom> loadChatRoomById(ChatRoomId chatRoomId);
    Optional<ChatRoom> loadPausedChatRoomByMemberId(MemberId memberId);

    Page<ChatRoom> loadAliveChatRoomsByMemberId(MemberId memberId, String keyword, Pageable pageable);

    boolean isMemberOwnerOfChatRooms(MemberId memberId, List<ChatRoomId> chatRoomIds);
}
