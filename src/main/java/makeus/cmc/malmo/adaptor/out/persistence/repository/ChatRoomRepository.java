package makeus.cmc.malmo.adaptor.out.persistence.repository;

import makeus.cmc.malmo.adaptor.out.persistence.entity.chat.ChatRoomEntity;
import makeus.cmc.malmo.adaptor.out.persistence.repository.custom.ChatRoomRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface ChatRoomRepository extends JpaRepository<ChatRoomEntity, Long>, ChatRoomRepositoryCustom {

    @Query("select c from ChatRoomEntity c where c.memberEntityId.value = ?1 AND c.chatRoomState != 'DELETED' AND c.chatRoomState != 'COMPLETED'")
    Optional<ChatRoomEntity> findCurrentChatRoomByMemberEntityId(Long memberId);

    @Query("select c from ChatRoomEntity c where c.memberEntityId.value = ?1 AND c.chatRoomState = 'PAUSED'")
    Optional<ChatRoomEntity> findPausedChatRoomByMemberEntityId(Long memberId);
}
