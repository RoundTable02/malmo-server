package makeus.cmc.malmo.application.port.out.notification;

import makeus.cmc.malmo.domain.model.notification.MemberNotification;

public interface SaveNotificationPort {

    MemberNotification saveNotification(MemberNotification memberNotification);
}
