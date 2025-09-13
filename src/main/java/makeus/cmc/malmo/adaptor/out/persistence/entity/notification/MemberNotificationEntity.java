package makeus.cmc.malmo.adaptor.out.persistence.entity.notification;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import makeus.cmc.malmo.adaptor.out.persistence.entity.BaseTimeEntity;
import makeus.cmc.malmo.adaptor.out.persistence.entity.value.MemberEntityId;
import makeus.cmc.malmo.domain.value.state.NotificationState;
import makeus.cmc.malmo.domain.value.type.NotificationType;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.Map;

@Getter
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class MemberNotificationEntity extends BaseTimeEntity {

    @Column(name = "memberNotificationId")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Embedded
    private MemberEntityId memberId;

    @Enumerated(value = EnumType.STRING)
    private NotificationType type;

    @Enumerated(value = EnumType.STRING)
    private NotificationState state;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "json")
    private Map<String, Object> payload;
}
