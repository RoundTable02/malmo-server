package makeus.cmc.malmo.application.port.in.notification;

import lombok.Builder;
import lombok.Data;
import makeus.cmc.malmo.domain.value.state.NotificationState;
import makeus.cmc.malmo.domain.value.type.NotificationType;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface GetPendingNotificationUseCase {

    GetPendingNotificationResponse getPendingNotifications(GetPendingNotificationCommand command);


    @Data
    @Builder
    class GetPendingNotificationResponse {
        private List<PendingNotificationData> pendingNotifications;
    }

    @Data
    @Builder
    class PendingNotificationData {
        private Long id;
        private NotificationType type;
        private NotificationState state;
        private Map<String, Object> payload;
        private LocalDateTime createdAt;
    }

    @Data
    @Builder
    class GetPendingNotificationCommand {
        private Long userId;
    }
}
