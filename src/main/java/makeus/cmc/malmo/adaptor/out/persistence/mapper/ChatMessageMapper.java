package makeus.cmc.malmo.adaptor.out.persistence.mapper;

import makeus.cmc.malmo.adaptor.out.persistence.entity.chat.ChatMessageEntity;
import makeus.cmc.malmo.adaptor.out.persistence.entity.value.ChatRoomEntityId;
import makeus.cmc.malmo.domain.model.chat.ChatMessage;
import makeus.cmc.malmo.domain.value.id.ChatRoomId;
import org.springframework.stereotype.Component;

@Component
public class ChatMessageMapper {

    public ChatMessage toDomain(ChatMessageEntity entity) {
        return ChatMessage.from(
                entity.getId(),
                entity.getChatRoomEntityId() != null ?
                        ChatRoomId.of(entity.getChatRoomEntityId().getValue()) : null,
                entity.getLevel(),
                entity.getContent(),
                entity.getSenderType(),
                entity.getCreatedAt(),
                entity.getModifiedAt(),
                entity.getDeletedAt()
        );
    }

    public ChatMessageEntity toEntity(ChatMessage domain) {
        if (domain == null) {
            return null;
        }

        return ChatMessageEntity.builder()
                .id(domain.getId())
                .chatRoomEntityId(domain.getChatRoomId() != null ?
                        ChatRoomEntityId.of(domain.getChatRoomId().getValue()) : null)
                .level(domain.getLevel())
                .content(domain.getContent())
                .senderType(domain.getSenderType())
                .createdAt(domain.getCreatedAt())
                .modifiedAt(domain.getModifiedAt())
                .deletedAt(domain.getDeletedAt())
                .build();
    }
}
