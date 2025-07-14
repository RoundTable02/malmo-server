package makeus.cmc.malmo.adaptor.out.persistence.repository.custom;

import makeus.cmc.malmo.application.port.out.LoadCurrentMessagesPort;

import java.util.List;

public interface ChatRoomRepositoryCustom {
    List<LoadCurrentMessagesPort.ChatRoomMessageRepositoryDto> loadCurrentMessagesDto(Long chatRoomId, int page, int size);
}
