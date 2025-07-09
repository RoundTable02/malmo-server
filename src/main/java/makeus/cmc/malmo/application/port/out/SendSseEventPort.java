package makeus.cmc.malmo.application.port.out;

import lombok.AllArgsConstructor;
import lombok.Getter;
import makeus.cmc.malmo.domain.model.value.MemberId;

public interface SendSseEventPort {
    void sendToMember(MemberId memberId, NotificationEvent event);

    @Getter
    @AllArgsConstructor
    class NotificationEvent {
        private String eventName;
        private Object data;
    }
}

