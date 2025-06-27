package makeus.cmc.malmo.adaptor.out.persistence.mapper;

import makeus.cmc.malmo.adaptor.out.persistence.entity.chat.ChatMessageEntity;
import makeus.cmc.malmo.adaptor.out.persistence.entity.chat.SenderTypeJpa;
import makeus.cmc.malmo.domain.model.chat.ChatMessage;
import makeus.cmc.malmo.domain.model.chat.SenderType;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class ChatMessageMapper {

    private final ChatRoomMapper chatRoomMapper;

    public ChatMessageMapper(ChatRoomMapper chatRoomMapper) {
        this.chatRoomMapper = chatRoomMapper;
    }

    public ChatMessage toDomain(ChatMessageEntity entity) {
        return ChatMessage.builder()
                .id(entity.getId())
                .chatRoom(chatRoomMapper.toDomain(entity.getChatRoom()))
                .isImage(entity.isImage())
                .imageUrl(entity.getImageUrl())
                .extractedText(entity.getExtractedText())
                .senderType(toSenderType(entity.getSenderTypeJpa()))
                .createdAt(entity.getCreatedAt())
                .modifiedAt(entity.getModifiedAt())
                .deletedAt(entity.getDeletedAt())
                .build();
    }

    public ChatMessageEntity toEntity(ChatMessage domain) {
        return ChatMessageEntity.builder()
                .id(domain.getId())
                .chatRoom(chatRoomMapper.toEntity(domain.getChatRoom()))
                .isImage(domain.isImage())
                .imageUrl(domain.getImageUrl())
                .extractedText(domain.getExtractedText())
                .senderTypeJpa(toSenderTypeJpa(domain.getSenderType()))
                .build();
    }

    private SenderType toSenderType(SenderTypeJpa senderTypeJpa) {
        return Optional.ofNullable(senderTypeJpa)
                .map(s -> SenderType.valueOf(s.name()))
                .orElse(null);
    }

    private SenderTypeJpa toSenderTypeJpa(SenderType senderType) {
        return Optional.ofNullable(senderType)
                .map(s -> SenderTypeJpa.valueOf(s.name()))
                .orElse(null);
    }

}