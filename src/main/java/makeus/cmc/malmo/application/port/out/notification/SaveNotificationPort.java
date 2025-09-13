package makeus.cmc.malmo.application.port.out.notification;

import makeus.cmc.malmo.domain.model.notification.MemberNotification;

import java.util.List;

public interface SaveNotificationPort {

    MemberNotification saveNotification(MemberNotification memberNotification);

    void readNotifications(List<Long> notificationIds);
}
