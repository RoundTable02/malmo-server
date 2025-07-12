package makeus.cmc.malmo.domain.service;

import lombok.RequiredArgsConstructor;
import makeus.cmc.malmo.application.port.out.LoadCurrentMessagesPort;
import makeus.cmc.malmo.application.port.out.SaveChatMessagePort;
import makeus.cmc.malmo.domain.model.chat.ChatMessage;
import makeus.cmc.malmo.domain.model.value.ChatRoomId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ChatMessagesDomainService {

    private final LoadCurrentMessagesPort loadMessagesPort;
    private final SaveChatMessagePort saveChatMessagePort;

    public List<ChatMessage> getChatMessages(ChatRoomId chatRoomId) {
        return loadMessagesPort.loadMessages(chatRoomId);
    }

    @Transactional
    public ChatMessage createUserTextMessage(ChatRoomId chatRoomId, String content) {
        ChatMessage chatMessage = ChatMessage.createUserTextMessage(chatRoomId, content);
        return saveChatMessagePort.saveChatMessage(chatMessage);
    }

    @Transactional
    public ChatMessage createAiTextMessage(ChatRoomId chatRoomId, String content) {
        ChatMessage chatMessage = ChatMessage.createAssistantTextMessage(chatRoomId, content);
        return saveChatMessagePort.saveChatMessage(chatMessage);
    }
}
