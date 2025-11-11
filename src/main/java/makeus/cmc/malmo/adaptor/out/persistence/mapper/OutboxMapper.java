package makeus.cmc.malmo.adaptor.out.persistence.mapper;

import makeus.cmc.malmo.adaptor.out.persistence.entity.OutboxEntity;
import makeus.cmc.malmo.domain.model.Outbox;
import org.springframework.stereotype.Component;

@Component
public class OutboxMapper {

    public Outbox toDomain(OutboxEntity entity) {
        if (entity == null) {
            return null;
        }

        return Outbox.from(
                entity.getId(),
                entity.getType(),
                entity.getPayload(),
                entity.getRetryCount(),
                entity.getState(),
                entity.getMessageId(),
                entity.getCreatedAt(),
                entity.getModifiedAt(),
                entity.getDeletedAt()
        );
    }

    public OutboxEntity toEntity(Outbox domain) {
        if (domain == null) {
            return null;
        }

        return OutboxEntity.builder()
                .id(domain.getId())
                .type(domain.getType())
                .payload(domain.getPayload())
                .retryCount(domain.getRetryCount())
                .state(domain.getState())
                .messageId(domain.getMessageId())
                .createdAt(domain.getCreatedAt())
                .modifiedAt(domain.getModifiedAt())
                .deletedAt(domain.getDeletedAt())
                .build();
    }
}
