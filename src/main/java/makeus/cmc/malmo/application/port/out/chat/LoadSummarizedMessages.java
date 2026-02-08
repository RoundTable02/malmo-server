package makeus.cmc.malmo.application.port.out.chat;

import makeus.cmc.malmo.domain.model.chat.ChatMessageSummary;
import makeus.cmc.malmo.domain.value.id.ChatRoomId;

import java.util.List;
import java.util.Optional;

public interface LoadSummarizedMessages {

    List<ChatMessageSummary> loadSummarizedMessages(ChatRoomId chatRoomId);

    Optional<ChatMessageSummary> loadLatestSummaryByLevel(ChatRoomId chatRoomId, int level);
}
