package makeus.cmc.malmo.adaptor.out.persistence.repository.custom;

import makeus.cmc.malmo.adaptor.out.persistence.entity.chat.ChatRoomEntity;
import makeus.cmc.malmo.application.port.out.LoadMessagesPort;
import makeus.cmc.malmo.domain.model.chat.ChatRoom;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ChatRoomRepositoryCustom {
    Page<ChatRoomEntity> loadChatRoomListByMemberId(Long memberId, String keyword, Pageable pageable);

    boolean isMemberOwnerOfChatRooms(Long memberId, List<Long> chatRoomIds);

    void deleteChatRooms(List<Long> chatRoomIds);

    int countChatRoomsByMemberId(Long memberId);
}
