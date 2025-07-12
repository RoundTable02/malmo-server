package makeus.cmc.malmo.adaptor.out.persistence.mapper;

import makeus.cmc.malmo.adaptor.out.persistence.entity.chat.ChatMessageEntity;
import makeus.cmc.malmo.adaptor.out.persistence.entity.value.ChatRoomEntityId;
import makeus.cmc.malmo.domain.model.chat.ChatMessage;
import makeus.cmc.malmo.domain.value.id.ChatRoomId;
import org.springframework.stereotype.Component;

@Component
public class ChatMessageMapper {

    public ChatMessage toDomain(ChatMessageEntity entity) {
        if (entity == null) {
            return null;
        }

        return ChatMessage.builder()
                .id(entity.getId())
                .chatRoomId(entity.getChatRoomEntityId() != null ?
                        ChatRoomId.of(entity.getChatRoomEntityId().getValue()) : null)
                .isImage(entity.isImage())
                .imageUrl(entity.getImageUrl())
                .extractedText(entity.getExtractedText())
                .content(entity.getContent())
                .senderType(entity.getSenderType())
                .createdAt(entity.getCreatedAt())
                .modifiedAt(entity.getModifiedAt())
                .build();
    }
    public ChatMessageEntity toEntity(ChatMessage domain) {
        if (domain == null) {
            return null;
        }

        return ChatMessageEntity.builder()
                .id(domain.getId())
                .chatRoomEntityId(domain.getChatRoomId() != null ?
                        ChatRoomEntityId.of(domain.getChatRoomId().getValue()) : null)
                .isImage(domain.isImage())
                .imageUrl(domain.getImageUrl())
                .extractedText(domain.getExtractedText())
                .content(domain.getContent())
                .senderType(domain.getSenderType())
                .createdAt(domain.getCreatedAt())
                .modifiedAt(domain.getModifiedAt())
                .build();
    }
}
