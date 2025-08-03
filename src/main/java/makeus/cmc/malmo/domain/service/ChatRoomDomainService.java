package makeus.cmc.malmo.domain.service;

import lombok.RequiredArgsConstructor;
import makeus.cmc.malmo.domain.model.chat.ChatMessage;
import makeus.cmc.malmo.domain.model.chat.ChatRoom;
import makeus.cmc.malmo.domain.value.id.ChatRoomId;
import makeus.cmc.malmo.domain.value.id.MemberId;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ChatRoomDomainService {

    public ChatRoom createChatRoom(MemberId memberId) {
        return ChatRoom.createChatRoom(memberId);
    }

    public ChatMessage createUserMessage(ChatRoomId chatRoomId, int level, String content) {
        return ChatMessage.createUserTextMessage(chatRoomId, level, content);
    }

    public ChatMessage createAiMessage(ChatRoomId chatRoomId, int level, String content) {
        return ChatMessage.createAssistantTextMessage(chatRoomId, level, content);
    }
}
