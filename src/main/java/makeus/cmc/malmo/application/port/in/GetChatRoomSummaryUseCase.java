package makeus.cmc.malmo.application.port.in;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

public interface GetChatRoomSummaryUseCase {

    GetChatRoomSummaryResponse getChatRoomSummary(GetChatRoomSummaryCommand command);

    @Data
    @Builder
    class GetChatRoomSummaryCommand {
        private Long userId;
        private Long chatRoomId;
    }

    @Data
    @Builder
    class GetChatRoomSummaryResponse {
        private Long chatRoomId;
        private LocalDateTime createdAt;
        private String totalSummary;
        private String firstSummary;
        private String secondSummary;
        private String thirdSummary;
    }
}
