package makeus.cmc.malmo.domain.service;

import lombok.RequiredArgsConstructor;
import makeus.cmc.malmo.application.port.out.LoadMessagesPort;
import makeus.cmc.malmo.application.port.out.LoadSummarizedMessages;
import makeus.cmc.malmo.application.port.out.SaveChatMessagePort;
import makeus.cmc.malmo.domain.model.chat.ChatMessage;
import makeus.cmc.malmo.domain.model.chat.ChatMessageSummary;
import makeus.cmc.malmo.domain.value.id.ChatRoomId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatMessagesDomainService {

    private final LoadMessagesPort loadMessagesPort;
    private final SaveChatMessagePort saveChatMessagePort;
    private final LoadSummarizedMessages loadSummarizedMessages;

    public Page<LoadMessagesPort.ChatRoomMessageRepositoryDto> getChatMessagesDto(ChatRoomId chatRoomId, Pageable pageable) {
        return loadMessagesPort.loadMessagesDto(chatRoomId, pageable);
    }

    public Page<LoadMessagesPort.ChatRoomMessageRepositoryDto> getChatMessagesDtoAsc(ChatRoomId chatRoomId, Pageable pageable) {
        return loadMessagesPort.loadMessagesDtoAsc(chatRoomId, pageable);
    }

    public ChatMessage createUserTextMessage(ChatRoomId chatRoomId, int level, String content) {
        ChatMessage chatMessage = ChatMessage.createUserTextMessage(chatRoomId, level, content);
        return saveChatMessagePort.saveChatMessage(chatMessage);
    }

    public ChatMessage createAiTextMessage(ChatRoomId chatRoomId, int level, String content) {
        ChatMessage chatMessage = ChatMessage.createAssistantTextMessage(chatRoomId, level, content);
        return saveChatMessagePort.saveChatMessage(chatMessage);
    }

    public List<ChatMessageSummary> getSummarizedMessages(ChatRoomId chatRoomId) {
        return loadSummarizedMessages.loadSummarizedMessages(chatRoomId);
    }

    public List<ChatMessage> getChatRoomLevelMessages(ChatRoomId chatRoomId, int level) {
        return loadMessagesPort.loadChatRoomMessagesByLevel(chatRoomId, level);
    }
}
