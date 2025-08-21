package makeus.cmc.malmo.application.helper.notification;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import makeus.cmc.malmo.application.exception.MemberAccessDeniedException;
import makeus.cmc.malmo.application.port.out.notification.LoadNotificationPort;
import makeus.cmc.malmo.domain.model.notification.MemberNotification;
import makeus.cmc.malmo.domain.value.id.MemberId;
import makeus.cmc.malmo.domain.value.state.NotificationState;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Component
public class MemberNotificationQueryHelper {

    private final LoadNotificationPort loadNotificationPort;

    public List<MemberNotification> getAllPendingNotifications(MemberId memberId) {
        return loadNotificationPort.getNotificationsByMemberIdAndState(memberId, NotificationState.PENDING);
    }

    public void validateMemberNotifications(List<Long> notificationIds, MemberId memberId) {
        boolean isValid = loadNotificationPort.validateAllNotificationsOwnership(memberId, notificationIds);

        if (!isValid) {
            throw new MemberAccessDeniedException("해당 알림에 접근 권한이 없습니다.");
        }
    }
}
