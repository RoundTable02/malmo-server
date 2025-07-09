package makeus.cmc.malmo.application.port.out;

import lombok.AllArgsConstructor;
import lombok.Getter;
import makeus.cmc.malmo.domain.model.value.MemberId;

public interface SendSseEventPort {
    void sendToMember(MemberId memberId, NotificationEvent event);

    @Getter
    @AllArgsConstructor
    class NotificationEvent {
        private SseEventType eventType;
        private Object data;
    }

    @Getter
    @AllArgsConstructor
    enum SseEventType {
        COUPLE_CONNECTED("couple_connected");

        private final String eventName;
    }
}

