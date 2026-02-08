package makeus.cmc.malmo.adaptor.out.persistence.repository.chat;

import makeus.cmc.malmo.application.port.out.chat.LoadMessagesPort;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ChatMessageRepositoryCustom {
    Page<LoadMessagesPort.ChatRoomMessageRepositoryDto> loadCurrentMessagesDto(Long chatRoomId, Long memberId, Pageable pageable);
    Page<LoadMessagesPort.ChatRoomMessageRepositoryDto> loadCurrentMessagesDtoAsc(Long chatRoomId, Long memberId, Pageable pageable);
}
