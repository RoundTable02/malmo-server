package makeus.cmc.malmo.adaptor.out.persistence.repository.chat;

import makeus.cmc.malmo.adaptor.out.persistence.entity.chat.ChatMessageSummaryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ChatMessageSummaryRepository extends JpaRepository<ChatMessageSummaryEntity, Long> {

    @Query("SELECT c FROM ChatMessageSummaryEntity c WHERE c.chatRoomEntityId.value = :chatRoomId")
    List<ChatMessageSummaryEntity> findSummarizedMessagesByChatRoomEntityId(Long chatRoomId);

    @Query("SELECT c FROM ChatMessageSummaryEntity c WHERE c.chatRoomEntityId.value = :chatRoomId AND c.level = :level ORDER BY c.createdAt DESC")
    List<ChatMessageSummaryEntity> findByChatRoomIdAndLevelOrderByCreatedAtDesc(@Param("chatRoomId") Long chatRoomId, @Param("level") int level);

    @Query("SELECT c FROM ChatMessageSummaryEntity c WHERE c.chatRoomEntityId.value = :chatRoomId AND c.level = :level ORDER BY c.createdAt DESC")
    Optional<ChatMessageSummaryEntity> findLatestByChatRoomIdAndLevel(@Param("chatRoomId") Long chatRoomId, @Param("level") int level);
}
