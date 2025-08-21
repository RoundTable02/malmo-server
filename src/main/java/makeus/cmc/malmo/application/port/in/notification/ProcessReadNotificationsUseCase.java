package makeus.cmc.malmo.application.port.in.notification;

import lombok.Builder;
import lombok.Data;

import java.util.List;

public interface ProcessReadNotificationsUseCase {

    void processReadNotifications(ProcessReadNotificationsCommand command);

    @Data
    @Builder
    class ProcessReadNotificationsCommand {
        private Long userId;
        private List<Long> notificationIds;
    }
}
