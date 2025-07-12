package makeus.cmc.malmo.application.port.out;

import makeus.cmc.malmo.domain.model.chat.ChatMessage;
import makeus.cmc.malmo.domain.model.value.ChatRoomId;

import java.util.List;

public interface LoadCurrentMessagesPort {
    List<ChatMessage> loadMessages(ChatRoomId chatRoomId);
}
