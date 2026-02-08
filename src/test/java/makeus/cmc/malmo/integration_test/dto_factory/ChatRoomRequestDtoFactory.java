package makeus.cmc.malmo.integration_test.dto_factory;

import makeus.cmc.malmo.adaptor.in.web.controller.ChatRoomController;

import java.util.List;

public class ChatRoomRequestDtoFactory {

    public static ChatRoomController.SendMessageRequest createSendChatMessageRequestDto(String message) {
        return new ChatRoomController.SendMessageRequest(message);
    }

    public static ChatRoomController.DeleteChatRoomRequestDto createDeleteChatRoomsRequestDto(List<Long> chatRoomIds) {
        ChatRoomController.DeleteChatRoomRequestDto dto = new ChatRoomController.DeleteChatRoomRequestDto();
        dto.setChatRoomIdList(chatRoomIds);
        return dto;
    }
}
