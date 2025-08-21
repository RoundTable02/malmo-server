package makeus.cmc.malmo.application.port.out.notification;

import makeus.cmc.malmo.domain.model.notification.MemberNotification;
import makeus.cmc.malmo.domain.value.id.MemberId;
import makeus.cmc.malmo.domain.value.state.NotificationState;

import java.util.List;

public interface LoadNotificationPort {

    List<MemberNotification> getNotificationsByMemberIdAndState(MemberId memberId, NotificationState state);
}
