package makeus.cmc.malmo.application.service.helper.chat_room;

import lombok.RequiredArgsConstructor;
import makeus.cmc.malmo.application.port.out.DeleteChatRoomPort;
import makeus.cmc.malmo.application.port.out.SaveChatMessagePort;
import makeus.cmc.malmo.application.port.out.SaveChatRoomPort;
import makeus.cmc.malmo.domain.model.chat.ChatMessage;
import makeus.cmc.malmo.domain.model.chat.ChatRoom;
import makeus.cmc.malmo.domain.value.id.ChatRoomId;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ChatRoomCommandHelper {

    private final SaveChatRoomPort saveChatRoomPort;
    private final DeleteChatRoomPort deleteChatRoomPort;

    private final SaveChatMessagePort saveChatMessagePort;

    public ChatRoom saveChatRoom(ChatRoom chatRoom) {
        return saveChatRoomPort.saveChatRoom(chatRoom);
    }

    public void deleteChatRooms(List<ChatRoomId> chatRoomIds) {
        deleteChatRoomPort.deleteChatRooms(chatRoomIds);
    }

    public ChatMessage saveChatMessage(ChatMessage chatMessage) {
        return saveChatMessagePort.saveChatMessage(chatMessage);
    }
}
