package makeus.cmc.malmo.application.port.out;

import makeus.cmc.malmo.domain.model.chat.ChatMessage;

public interface SaveChatMessagePort {
    ChatMessage saveChatMessage(ChatMessage chatMessage);
}
