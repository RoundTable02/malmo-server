package makeus.cmc.malmo.domain.service;

import lombok.RequiredArgsConstructor;
import makeus.cmc.malmo.application.port.out.LoadChatRoomPort;
import makeus.cmc.malmo.application.port.out.SaveChatRoomPort;
import makeus.cmc.malmo.domain.model.chat.ChatRoom;
import makeus.cmc.malmo.domain.value.id.MemberId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ChatRoomDomainService {
    private final LoadChatRoomPort loadChatRoomPort;
    private final SaveChatRoomPort saveChatRoomPort;

    public ChatRoom getCurrentChatRoomByMemberId(MemberId memberId) {
        // 현재 채팅방이 존재하지 않는 경우 새로 채팅방을 생성
        return loadChatRoomPort.loadCurrentChatRoomByMemberId(memberId)
                .orElseGet(() -> saveChatRoomPort.saveChatRoom(
                        ChatRoom.createChatRoom(memberId)
                ));
    }
}
