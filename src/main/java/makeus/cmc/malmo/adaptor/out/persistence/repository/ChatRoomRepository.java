package makeus.cmc.malmo.adaptor.out.persistence.repository;

import makeus.cmc.malmo.adaptor.out.persistence.entity.chat.ChatRoomEntity;
import makeus.cmc.malmo.adaptor.out.persistence.entity.couple.CoupleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface ChatRoomRepository extends JpaRepository<ChatRoomEntity, Long> {

    @Query("select c from ChatRoomEntity c where c.memberEntityId.value = ?1 AND c.chatRoomStateJpa = 'ALIVE'")
    Optional<ChatRoomEntity> findChatRoomEntityByMemberEntityId_Value(Long memberId);
}
