package makeus.cmc.malmo.integration_test.dto_factory;

import makeus.cmc.malmo.adaptor.in.web.controller.ChatRoomController;
import makeus.cmc.malmo.adaptor.in.web.controller.CurrentChatController;

import java.util.List;

public class ChatRoomRequestDtoFactory {

    public static CurrentChatController.ChatRequest createSendChatMessageRequestDto(String message) {
        return new CurrentChatController.ChatRequest(message);
    }

    public static ChatRoomController.DeleteChatRoomRequestDto createDeleteChatRoomsRequestDto(List<Long> chatRoomIds) {
        ChatRoomController.DeleteChatRoomRequestDto dto = new ChatRoomController.DeleteChatRoomRequestDto();
        dto.setChatRoomIdList(chatRoomIds);
        return dto;
    }
}
