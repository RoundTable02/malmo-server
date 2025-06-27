package makeus.cmc.malmo.adaptor.out.persistence.mapper;

import makeus.cmc.malmo.adaptor.out.persistence.entity.chat.ChatRoomEntity;
import makeus.cmc.malmo.adaptor.out.persistence.entity.chat.ChatRoomStateJpa;
import makeus.cmc.malmo.domain.model.chat.ChatRoom;
import makeus.cmc.malmo.domain.model.chat.ChatRoomState;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class ChatRoomMapper {

    private final MemberMapper memberMapper;

    public ChatRoomMapper(MemberMapper memberMapper) {
        this.memberMapper = memberMapper;
    }

    public ChatRoom toDomain(ChatRoomEntity entity) {
        return ChatRoom.builder()
                .id(entity.getId())
                .member(memberMapper.toDomain(entity.getMember()))
                .chatRoomState(toChatRoomState(entity.getChatRoomStateJpa()))
                .createdAt(entity.getCreatedAt())
                .modifiedAt(entity.getModifiedAt())
                .deletedAt(entity.getDeletedAt())
                .build();
    }

    public ChatRoomEntity toEntity(ChatRoom domain) {
        return ChatRoomEntity.builder()
                .id(domain.getId())
                .member(memberMapper.toEntity(domain.getMember()))
                .chatRoomStateJpa(toChatRoomStateJpa(domain.getChatRoomState()))
                .build();
    }

    public ChatRoomState toChatRoomState(ChatRoomStateJpa chatRoomStateJpa) {
        return Optional.ofNullable(chatRoomStateJpa)
                .map(state -> ChatRoomState.valueOf(state.name()))
                .orElse(null);
    }

    public ChatRoomStateJpa toChatRoomStateJpa(ChatRoomState chatRoomState) {
        return Optional.ofNullable(chatRoomState)
                .map(state -> ChatRoomStateJpa.valueOf(state.name()))
                .orElse(null);
    }
}