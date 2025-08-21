package makeus.cmc.malmo.application.helper.notification;

import lombok.RequiredArgsConstructor;
import makeus.cmc.malmo.application.port.out.notification.LoadNotificationPort;
import makeus.cmc.malmo.domain.model.notification.MemberNotification;
import makeus.cmc.malmo.domain.value.id.MemberId;
import makeus.cmc.malmo.domain.value.state.NotificationState;
import org.springframework.stereotype.Component;

import java.util.List;

@RequiredArgsConstructor
@Component
public class MemberNotificationQueryHelper {

    private final LoadNotificationPort loadNotificationPort;

    public List<MemberNotification> getAllPendingNotifications(MemberId memberId) {
        return loadNotificationPort.getNotificationsByMemberIdAndState(memberId, NotificationState.PENDING);
    }
}
