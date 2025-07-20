package makeus.cmc.malmo.application.port.out;

import makeus.cmc.malmo.domain.model.chat.ChatRoom;

public interface SaveChatRoomPort {
    ChatRoom saveChatRoom(ChatRoom chatRoom);
}
