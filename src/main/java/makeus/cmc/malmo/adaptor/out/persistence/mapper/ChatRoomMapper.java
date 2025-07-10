package makeus.cmc.malmo.adaptor.out.persistence.mapper;

import lombok.extern.slf4j.Slf4j;
import makeus.cmc.malmo.adaptor.out.persistence.entity.chat.ChatRoomEntity;
import makeus.cmc.malmo.adaptor.out.persistence.entity.chat.ChatRoomStateJpa;
import makeus.cmc.malmo.adaptor.out.persistence.entity.value.MemberEntityId;
import makeus.cmc.malmo.domain.model.chat.ChatRoom;
import makeus.cmc.malmo.domain.model.chat.ChatRoomState;
import makeus.cmc.malmo.domain.model.value.MemberId;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ChatRoomMapper {

    public ChatRoom toDomain(ChatRoomEntity chatRoomEntity) {
        if (chatRoomEntity == null) {
            return null;
        }

        return ChatRoom.builder()
                .id(chatRoomEntity.getId())
                .memberId(chatRoomEntity.getMemberEntityId() != null ? MemberId.of(chatRoomEntity.getMemberEntityId().getValue()) : null)
                .chatRoomState(toChatRoomState(chatRoomEntity.getChatRoomStateJpa()))
                .createdAt(chatRoomEntity.getCreatedAt())
                .modifiedAt(chatRoomEntity.getModifiedAt())
                .deletedAt(chatRoomEntity.getDeletedAt())
                .build();
    }

    public ChatRoomEntity toEntity(ChatRoom chatRoom) {
        if (chatRoom == null) {
            return null;
        }

        return ChatRoomEntity.builder()
                .id(chatRoom.getId())
                .memberEntityId(chatRoom.getMemberId() != null ? MemberEntityId.of(chatRoom.getMemberId().getValue()) : null)
                .chatRoomStateJpa(toChatRoomStateJpa(chatRoom.getChatRoomState()))
                .createdAt(chatRoom.getCreatedAt())
                .modifiedAt(chatRoom.getModifiedAt())
                .deletedAt(chatRoom.getDeletedAt())
                .build();
    }

    public ChatRoomState toChatRoomState(ChatRoomStateJpa chatRoomStateJpa) {
        return chatRoomStateJpa != null ? ChatRoomState.valueOf(chatRoomStateJpa.name()) : null;
    }

    public ChatRoomStateJpa toChatRoomStateJpa(ChatRoomState chatRoomState) {
        return chatRoomState != null ? ChatRoomStateJpa.valueOf(chatRoomState.name()) : null;
    }
}
