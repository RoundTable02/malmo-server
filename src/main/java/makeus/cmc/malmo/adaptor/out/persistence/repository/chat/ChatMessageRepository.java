package makeus.cmc.malmo.adaptor.out.persistence.repository.chat;

import makeus.cmc.malmo.adaptor.out.persistence.entity.chat.ChatMessageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ChatMessageRepository extends JpaRepository<ChatMessageEntity, Long>, ChatMessageRepositoryCustom {

    @Query("SELECT c FROM ChatMessageEntity c WHERE c.chatRoomEntityId.value = :chatRoomId AND c.level = :level")
    List<ChatMessageEntity> findByChatRoomIdAndLevel(Long chatRoomId, int level);
}
