package makeus.cmc.malmo.adaptor.out.persistence.adapter;

import lombok.RequiredArgsConstructor;
import makeus.cmc.malmo.adaptor.out.persistence.mapper.ChatMessageSummaryMapper;
import makeus.cmc.malmo.adaptor.out.persistence.repository.chat.ChatMessageSummaryRepository;
import makeus.cmc.malmo.application.port.out.chat.LoadSummarizedMessages;
import makeus.cmc.malmo.domain.model.chat.ChatMessageSummary;
import makeus.cmc.malmo.domain.value.id.ChatRoomId;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Component
public class ChatMessageSummaryPersistenceAdapter
        implements LoadSummarizedMessages {

    private final ChatMessageSummaryRepository chatMessageSummaryRepository;
    private final ChatMessageSummaryMapper chatMessageSummaryMapper;

    @Override
    public List<ChatMessageSummary> loadSummarizedMessages(ChatRoomId chatRoomId) {
        return chatMessageSummaryRepository.findSummarizedMessagesByChatRoomEntityId(chatRoomId.getValue())
                .stream()
                .map(chatMessageSummaryMapper::toDomain)
                .toList();
    }

    @Override
    public Optional<ChatMessageSummary> loadLatestSummaryByLevel(ChatRoomId chatRoomId, int level) {
        return chatMessageSummaryRepository.findLatestByChatRoomIdAndLevel(chatRoomId.getValue(), level)
                .map(chatMessageSummaryMapper::toDomain);
    }
}
