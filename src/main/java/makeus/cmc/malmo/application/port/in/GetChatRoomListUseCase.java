package makeus.cmc.malmo.application.port.in;

import lombok.Builder;
import lombok.Data;
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
        private String totalSummary;
        private String situationKeyword;
        private String solutionKeyword;
        private LocalDateTime createdAt;
    }
}
