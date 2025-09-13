package makeus.cmc.malmo.domain.model.notification;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import makeus.cmc.malmo.domain.value.id.MemberId;
import makeus.cmc.malmo.domain.value.state.NotificationState;
import makeus.cmc.malmo.domain.value.type.NotificationType;

import java.time.LocalDateTime;
import java.util.Map;

@Getter
@Builder(access = AccessLevel.PRIVATE)
public class MemberNotification {
    private Long id;
    private MemberId memberId;
    private NotificationType type;
    private NotificationState state;
    private Map<String, Object> payload;

    // BaseTimeEntity fields
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;
    private LocalDateTime deletedAt;

    public static MemberNotification createMemberNotification(
            MemberId memberId,
            NotificationType type,
            NotificationState state,
            Map<String, Object> payload) {

        return MemberNotification.builder()
                .memberId(memberId)
                .type(type)
                .state(state)
                .payload(payload)
                .build();
    }

    public static MemberNotification from(
            Long id,
            MemberId memberId,
            NotificationType type,
            NotificationState state,
            Map<String, Object> payload,
            LocalDateTime createdAt,
            LocalDateTime modifiedAt,
            LocalDateTime deletedAt) {

        return MemberNotification.builder()
                .id(id)
                .memberId(memberId)
                .type(type)
                .state(state)
                .payload(payload)
                .createdAt(createdAt)
                .modifiedAt(modifiedAt)
                .deletedAt(deletedAt)
                .build();
    }
}
