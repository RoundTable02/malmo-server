package makeus.cmc.malmo.adaptor.out.persistence.repository.chat;

import makeus.cmc.malmo.adaptor.out.persistence.entity.chat.ChatRoomEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ChatRoomRepository extends JpaRepository<ChatRoomEntity, Long>, ChatRoomRepositoryCustom {

    // 진행 중인 채팅방 목록 조회 (ALIVE 상태만)
    @Query("SELECT c FROM ChatRoomEntity c WHERE c.memberEntityId.value = ?1 AND c.chatRoomState = 'ALIVE' ORDER BY c.lastMessageSentTime DESC")
    List<ChatRoomEntity> findActiveChatRoomsByMemberEntityId(Long memberId);
}
