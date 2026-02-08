package makeus.cmc.malmo.application.port.in.chat;

import lombok.Builder;
import lombok.Data;
import makeus.cmc.malmo.domain.value.state.ChatRoomState;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

public interface GetChatRoomListUseCase {

    GetChatRoomListResponse getChatRoomList(GetChatRoomListCommand command);

    @Data
    @Builder
    class GetChatRoomListCommand {
        private Long userId;
        private String keyword;
        private Pageable pageable;
    }

    @Data
    @Builder
    class GetChatRoomListResponse {
        private List<GetChatRoomResponse> chatRoomList;
        private Long totalCount;
    }

    @Data
    @Builder
    class GetChatRoomResponse {
        private Long chatRoomId;
        private String title;  // 제목 (nullable)
        private ChatRoomState chatRoomState;  // 상태
        private int level;  // 현재 단계
        private LocalDateTime lastMessageSentTime;
        private LocalDateTime createdAt;
    }
}
