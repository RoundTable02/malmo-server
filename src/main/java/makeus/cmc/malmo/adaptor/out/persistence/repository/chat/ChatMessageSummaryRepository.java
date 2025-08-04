package makeus.cmc.malmo.adaptor.out.persistence.repository.chat;

import makeus.cmc.malmo.adaptor.out.persistence.entity.chat.ChatMessageSummaryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ChatMessageSummaryRepository extends JpaRepository<ChatMessageSummaryEntity, Long> {

    @Query("SELECT c FROM ChatMessageSummaryEntity c WHERE c.chatRoomEntityId.value = :chatRoomId")
    List<ChatMessageSummaryEntity> findSummarizedMessagesByChatRoomEntityId(Long chatRoomId);
}
