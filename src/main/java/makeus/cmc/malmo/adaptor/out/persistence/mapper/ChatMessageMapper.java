package makeus.cmc.malmo.adaptor.out.persistence.mapper;

import makeus.cmc.malmo.adaptor.out.persistence.entity.chat.ChatMessageEntity;
import makeus.cmc.malmo.adaptor.out.persistence.entity.chat.SenderTypeJpa;
import makeus.cmc.malmo.adaptor.out.persistence.entity.value.ChatRoomEntityId;
import makeus.cmc.malmo.domain.model.chat.ChatMessage;
import makeus.cmc.malmo.domain.model.chat.SenderType;
import makeus.cmc.malmo.domain.model.value.ChatRoomId;
import org.springframework.stereotype.Component;

@Component
public class ChatMessageMapper {

    public ChatMessage toDomain(ChatMessageEntity chatMessageEntity) {
        if (chatMessageEntity == null) {
            return null;
        }

        return ChatMessage.builder()
                .id(chatMessageEntity.getId())
                .chatRoomId(chatMessageEntity.getChatRoomEntityId() != null ?
                        ChatRoomId.of(chatMessageEntity.getChatRoomEntityId().getValue()) : null)
                .isImage(chatMessageEntity.isImage())
                .imageUrl(chatMessageEntity.getImageUrl())
                .extractedText(chatMessageEntity.getExtractedText())
                .content(chatMessageEntity.getContent())
                .senderType(toSenderType(chatMessageEntity.getSenderTypeJpa()))
                .createdAt(chatMessageEntity.getCreatedAt())
                .modifiedAt(chatMessageEntity.getModifiedAt())
                .build();
    }
    public ChatMessageEntity toEntity(ChatMessage chatMessage) {
        if (chatMessage == null) {
            return null;
        }

        return ChatMessageEntity.builder()
                .id(chatMessage.getId())
                .chatRoomEntityId(chatMessage.getChatRoomId() != null ?
                        ChatRoomEntityId.of(chatMessage.getChatRoomId().getValue()) : null)
                .isImage(chatMessage.isImage())
                .imageUrl(chatMessage.getImageUrl())
                .extractedText(chatMessage.getExtractedText())
                .content(chatMessage.getContent())
                .senderTypeJpa(toSenderTypeJpa(chatMessage.getSenderType()))
                .createdAt(chatMessage.getCreatedAt())
                .modifiedAt(chatMessage.getModifiedAt())
                .build();
    }

    public SenderType toSenderType(SenderTypeJpa senderType) {
        return senderType != null ? SenderType.valueOf(senderType.name()) : null;
    }

    public SenderTypeJpa toSenderTypeJpa(SenderType senderType) {
        return senderType != null ? SenderTypeJpa.valueOf(senderType.name()) : null;
    }
}
