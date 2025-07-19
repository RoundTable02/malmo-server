package makeus.cmc.malmo.adaptor.out.persistence.repository.custom;

import makeus.cmc.malmo.application.port.out.LoadMessagesPort;

import java.util.List;

public interface ChatMessageRepositoryCustom {
    List<LoadMessagesPort.ChatRoomMessageRepositoryDto> loadCurrentMessagesDto(Long chatRoomId, int page, int size);
    void updateChatMessageSummarizedAllTrue(Long chatRoomId);
}
