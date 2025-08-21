package makeus.cmc.malmo.mapper;

import makeus.cmc.malmo.adaptor.out.persistence.entity.notification.MemberNotificationEntity;
import makeus.cmc.malmo.adaptor.out.persistence.entity.value.MemberEntityId;
import makeus.cmc.malmo.adaptor.out.persistence.mapper.MemberNotificationMapper;
import makeus.cmc.malmo.domain.model.notification.MemberNotification;
import makeus.cmc.malmo.domain.value.id.MemberId;
import makeus.cmc.malmo.domain.value.state.NotificationState;
import makeus.cmc.malmo.domain.value.type.NotificationType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
@DisplayName("MemberNotificationMapper 테스트")
class MemberNotificationMapperTest {

    @InjectMocks
    private MemberNotificationMapper memberNotificationMapper;

    @Test
    @DisplayName("Entity를 Domain으로 변환한다")
    void toDomain() {
        // given
        LocalDateTime now = LocalDateTime.now();
        Map<String, Object> payload = Map.of("key", "value");
        MemberNotificationEntity entity = MemberNotificationEntity.builder()
                .id(1L)
                .memberId(MemberEntityId.of(100L))
                .type(NotificationType.COUPLE_DISCONNECTED)
                .state(NotificationState.PENDING)
                .payload(payload)
                .createdAt(now)
                .modifiedAt(now)
                .deletedAt(null)
                .build();

        // when
        MemberNotification domain = memberNotificationMapper.toDomain(entity);

        // then
        assertThat(domain.getId()).isEqualTo(entity.getId());
        assertThat(domain.getMemberId().getValue()).isEqualTo(entity.getMemberId().getValue());
        assertThat(domain.getType()).isEqualTo(entity.getType());
        assertThat(domain.getState()).isEqualTo(entity.getState());
        assertThat(domain.getPayload()).isEqualTo(entity.getPayload());
        assertThat(domain.getCreatedAt()).isEqualTo(entity.getCreatedAt());
        assertThat(domain.getModifiedAt()).isEqualTo(entity.getModifiedAt());
        assertThat(domain.getDeletedAt()).isEqualTo(entity.getDeletedAt());
    }

    @Test
    @DisplayName("Domain을 Entity로 변환한다")
    void toEntity() {
        // given
        LocalDateTime now = LocalDateTime.now();
        Map<String, Object> payload = Map.of("key", "value");
        MemberNotification domain = MemberNotification.from(
                1L,
                MemberId.of(100L),
                NotificationType.COUPLE_DISCONNECTED,
                NotificationState.PENDING,
                payload,
                now,
                now,
                null
        );

        // when
        MemberNotificationEntity entity = memberNotificationMapper.toEntity(domain);

        // then
        assertThat(entity.getId()).isEqualTo(domain.getId());
        assertThat(entity.getMemberId().getValue()).isEqualTo(domain.getMemberId().getValue());
        assertThat(entity.getType()).isEqualTo(domain.getType());
        assertThat(entity.getState()).isEqualTo(domain.getState());
        assertThat(entity.getPayload()).isEqualTo(domain.getPayload());
        assertThat(entity.getCreatedAt()).isEqualTo(domain.getCreatedAt());
        assertThat(entity.getModifiedAt()).isEqualTo(domain.getModifiedAt());
        assertThat(entity.getDeletedAt()).isEqualTo(domain.getDeletedAt());
    }
}