package makeus.cmc.malmo.adaptor.out.persistence.mapper;

import makeus.cmc.malmo.adaptor.out.persistence.entity.notification.MemberNotificationEntity;
import makeus.cmc.malmo.adaptor.out.persistence.entity.value.MemberEntityId;
import makeus.cmc.malmo.domain.model.notification.MemberNotification;
import makeus.cmc.malmo.domain.value.id.MemberId;
import org.springframework.stereotype.Component;

@Component
public class MemberNotificationMapper {

    public MemberNotification toDomain(MemberNotificationEntity entity) {
        return MemberNotification.from(
                entity.getId(),
                entity.getMemberId() == null ? null : MemberId.of(entity.getMemberId().getValue()),
                entity.getType(),
                entity.getState(),
                entity.getPayload(),
                entity.getCreatedAt(),
                entity.getModifiedAt(),
                entity.getDeletedAt()
        );
    }

    public MemberNotificationEntity toEntity(MemberNotification domain) {
        return MemberNotificationEntity.builder()
                .id(domain.getId())
                .memberId(domain.getMemberId() == null ? null : MemberEntityId.of(domain.getMemberId().getValue()))
                .type(domain.getType())
                .state(domain.getState())
                .payload(domain.getPayload())
                .createdAt(domain.getCreatedAt())
                .modifiedAt(domain.getModifiedAt())
                .deletedAt(domain.getDeletedAt())
                .build();
    }
}
