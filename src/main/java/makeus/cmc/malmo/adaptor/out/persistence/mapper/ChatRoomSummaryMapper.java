package makeus.cmc.malmo.adaptor.out.persistence.mapper;

import makeus.cmc.malmo.adaptor.out.persistence.entity.chat.ChatRoomSummaryEntity;
import makeus.cmc.malmo.domain.model.chat.ChatRoomSummary;
import org.springframework.stereotype.Component;

@Component
public class ChatRoomSummaryMapper {

    private final ChatRoomMapper chatRoomMapper;

    public ChatRoomSummaryMapper(ChatRoomMapper chatRoomMapper) {
        this.chatRoomMapper = chatRoomMapper;
    }

    public ChatRoomSummary toDomain(ChatRoomSummaryEntity entity) {
        return ChatRoomSummary.builder()
                .id(entity.getId())
                .chatRoom(chatRoomMapper.toDomain(entity.getChatRoom()))
                .content(entity.getContent())
                .createdAt(entity.getCreatedAt())
                .modifiedAt(entity.getModifiedAt())
                .deletedAt(entity.getDeletedAt())
                .build();
    }

    public ChatRoomSummaryEntity toEntity(ChatRoomSummary domain) {
        return ChatRoomSummaryEntity.builder()
                .id(domain.getId())
                .chatRoom(chatRoomMapper.toEntity(domain.getChatRoom()))
                .content(domain.getContent())
                .build();
    }
}