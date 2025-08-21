package makeus.cmc.malmo.application.service.notification;

import lombok.RequiredArgsConstructor;
import makeus.cmc.malmo.application.helper.notification.MemberNotificationQueryHelper;
import makeus.cmc.malmo.application.port.in.notification.GetPendingNotificationUseCase;
import makeus.cmc.malmo.domain.model.notification.MemberNotification;
import makeus.cmc.malmo.domain.value.id.MemberId;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService implements GetPendingNotificationUseCase {

    private final MemberNotificationQueryHelper memberNotificationQueryHelper;

    @Override
    public GetPendingNotificationResponse getPendingNotifications(GetPendingNotificationCommand command) {
        List<MemberNotification> pendingNotifications = memberNotificationQueryHelper.getAllPendingNotifications(MemberId.of(command.getUserId()));

        List<PendingNotificationData> notificationDataList = pendingNotifications.stream()
                .map(notification ->
                        PendingNotificationData.builder()
                                .id(notification.getId())
                                .type(notification.getType())
                                .state(notification.getState())
                                .payload(notification.getPayload())
                                .createdAt(notification.getCreatedAt())
                                .build()).toList();

        return GetPendingNotificationResponse.builder()
                .pendingNotifications(notificationDataList)
                .build();
    }
}
