package makeus.cmc.malmo.adaptor.out.persistence.repository.custom;

import makeus.cmc.malmo.adaptor.out.persistence.entity.chat.ChatRoomEntity;
import makeus.cmc.malmo.application.port.out.LoadMessagesPort;
import makeus.cmc.malmo.domain.model.chat.ChatRoom;

import java.util.List;

public interface ChatRoomRepositoryCustom {
    List<ChatRoomEntity> loadChatRoomListByMemberId(Long memberId, String keyword, int page, int size);
}
