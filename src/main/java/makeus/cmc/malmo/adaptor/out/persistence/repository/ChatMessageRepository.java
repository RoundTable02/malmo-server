package makeus.cmc.malmo.adaptor.out.persistence.repository;

import makeus.cmc.malmo.adaptor.out.persistence.entity.chat.ChatMessageEntity;
import makeus.cmc.malmo.adaptor.out.persistence.repository.custom.ChatRoomRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ChatMessageRepository extends JpaRepository<ChatMessageEntity, Long>, ChatRoomRepositoryCustom {

    @Query("SELECT c FROM ChatMessageEntity c WHERE c.chatRoomEntityId.value = :chatRoomId")
    List<ChatMessageEntity> findByChatRoomId(Long chatRoomId);
}
