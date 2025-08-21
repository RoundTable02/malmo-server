package makeus.cmc.malmo.application.service.notification;

import lombok.RequiredArgsConstructor;
import makeus.cmc.malmo.adaptor.in.aop.CheckValidMember;
import makeus.cmc.malmo.application.helper.notification.MemberNotificationCommandHelper;
import makeus.cmc.malmo.application.helper.notification.MemberNotificationQueryHelper;
import makeus.cmc.malmo.application.port.in.notification.GetPendingNotificationUseCase;
import makeus.cmc.malmo.application.port.in.notification.ProcessReadNotificationsUseCase;
import makeus.cmc.malmo.domain.model.notification.MemberNotification;
import makeus.cmc.malmo.domain.value.id.MemberId;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService implements GetPendingNotificationUseCase, ProcessReadNotificationsUseCase {

    private final MemberNotificationQueryHelper memberNotificationQueryHelper;
    private final MemberNotificationCommandHelper memberNotificationCommandHelper;

    @Override
    @CheckValidMember
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

    @Override
    @CheckValidMember
    public void processReadNotifications(ProcessReadNotificationsCommand command) {
        // 알림에 대한 접근 권한 확인
        memberNotificationQueryHelper.validateMemberNotifications(command.getNotificationIds(), MemberId.of(command.getUserId()));

        // 알림을 읽음 상태로 변경
        memberNotificationCommandHelper.markNotificationsAsRead(command.getNotificationIds());
    }
}
