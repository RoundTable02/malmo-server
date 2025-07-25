package makeus.cmc.malmo.adaptor.out.persistence.repository.custom;

import makeus.cmc.malmo.application.port.out.LoadMessagesPort;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ChatMessageRepositoryCustom {
    Page<LoadMessagesPort.ChatRoomMessageRepositoryDto> loadCurrentMessagesDto(Long chatRoomId, Pageable pageable);
    Page<LoadMessagesPort.ChatRoomMessageRepositoryDto> loadCurrentMessagesDtoAsc(Long chatRoomId, Pageable pageable);
}
