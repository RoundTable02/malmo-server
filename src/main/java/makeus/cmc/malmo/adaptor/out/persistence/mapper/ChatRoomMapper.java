package makeus.cmc.malmo.adaptor.out.persistence.mapper;

import lombok.extern.slf4j.Slf4j;
import makeus.cmc.malmo.adaptor.out.persistence.entity.chat.ChatRoomEntity;
import makeus.cmc.malmo.adaptor.out.persistence.entity.value.MemberEntityId;
import makeus.cmc.malmo.domain.model.chat.ChatRoom;
import makeus.cmc.malmo.domain.value.id.MemberId;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ChatRoomMapper {

    public ChatRoom toDomain(ChatRoomEntity entity) {
        if (entity == null) {
            return null;
        }

        return ChatRoom.builder()
                .id(entity.getId())
                .memberId(entity.getMemberEntityId() != null ? MemberId.of(entity.getMemberEntityId().getValue()) : null)
                .chatRoomState(entity.getChatRoomState())
                .createdAt(entity.getCreatedAt())
                .modifiedAt(entity.getModifiedAt())
                .deletedAt(entity.getDeletedAt())
                .build();
    }

    public ChatRoomEntity toEntity(ChatRoom domain) {
        if (domain == null) {
            return null;
        }

        return ChatRoomEntity.builder()
                .id(domain.getId())
                .memberEntityId(domain.getMemberId() != null ? MemberEntityId.of(domain.getMemberId().getValue()) : null)
                .chatRoomState(domain.getChatRoomState())
                .createdAt(domain.getCreatedAt())
                .modifiedAt(domain.getModifiedAt())
                .deletedAt(domain.getDeletedAt())
                .build();
    }
}
