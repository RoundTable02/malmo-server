package makeus.cmc.malmo.application.port.in;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

public interface GetChatRoomListUseCase {

    GetChatRoomListResponse getChatRoomList(GetChatRoomListCommand command);

    @Data
    @Builder
    class GetChatRoomListCommand {
        private Long userId;
        private String keyword;
        private Integer page;
        private Integer size;
    }

    @Data
    @Builder
    class GetChatRoomListResponse {
        private List<GetChatRoomResponse> chatRoomList;
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
