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
        // TODO : 채팅방 생성 시, 이전 채팅방 중 하나 이상의 채팅방의 isCurrentPromptForMetadata가 false인 경우
        //  -> LEVEL을 isCurrentPromptForMetadata가 false인 Prompt 중 가장 작은 LEVEL로 설정
        //  isCurrentPromptForMetadata가 false인 채팅방이 존재하지 않는 경우 가장 큰 LEVEL의 채팅방의 LEVEL로 설정
        return loadChatRoomPort.loadCurrentChatRoomByMemberId(memberId)
                .orElseGet(() -> saveChatRoomPort.saveChatRoom(
                        ChatRoom.createChatRoom(memberId)
                ));
    }
}
