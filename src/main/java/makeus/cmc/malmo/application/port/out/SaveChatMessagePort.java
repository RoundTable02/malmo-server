package makeus.cmc.malmo.application.port.out;

import makeus.cmc.malmo.domain.model.chat.ChatMessage;
import makeus.cmc.malmo.domain.model.chat.ChatRoom;

public interface SaveChatMessagePort {
    ChatMessage saveChatMessage(ChatMessage chatMessage);
}
