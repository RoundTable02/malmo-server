package makeus.cmc.malmo.adaptor.out.persistence.mapper;

import makeus.cmc.malmo.adaptor.out.persistence.entity.chat.SavedChatMessageEntity;
import makeus.cmc.malmo.adaptor.out.persistence.entity.chat.SavedChatMessageStateJpa;
import makeus.cmc.malmo.domain.model.chat.SavedChatMessage;
import makeus.cmc.malmo.domain.model.chat.SavedChatMessageState;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class SavedChatMessageMapper {

    private final ChatMessageMapper chatMessageMapper;
    private final MemberMapper memberMapper;

    public SavedChatMessageMapper(ChatMessageMapper chatMessageMapper, MemberMapper memberMapper) {
        this.chatMessageMapper = chatMessageMapper;
        this.memberMapper = memberMapper;
    }

    public SavedChatMessage toDomain(SavedChatMessageEntity entity) {
        return SavedChatMessage.builder()
                .id(entity.getId())
                .chatMessage(chatMessageMapper.toDomain(entity.getChatMessage()))
                .member(memberMapper.toDomain(entity.getMember()))
                .savedChatMessageState(toSavedChatMessageState(entity.getSavedChatMessageStateJpa()))
                .createdAt(entity.getCreatedAt())
                .modifiedAt(entity.getModifiedAt())
                .deletedAt(entity.getDeletedAt())
                .build();
    }

    public SavedChatMessageEntity toEntity(SavedChatMessage domain) {
        return SavedChatMessageEntity.builder()
                .id(domain.getId())
                .chatMessage(chatMessageMapper.toEntity(domain.getChatMessage()))
                .member(memberMapper.toEntity(domain.getMember()))
                .savedChatMessageStateJpa(toSavedChatMessageStateJpa(domain.getSavedChatMessageState()))
                .build();
    }

    private SavedChatMessageState toSavedChatMessageState(SavedChatMessageStateJpa savedChatMessageStateJpa) {
        return Optional.ofNullable(savedChatMessageStateJpa)
                .map(scms -> SavedChatMessageState.valueOf(scms.name()))
                .orElse(null);
    }

    private SavedChatMessageStateJpa toSavedChatMessageStateJpa(SavedChatMessageState savedChatMessageState) {
        return Optional.ofNullable(savedChatMessageState)
                .map(scms -> SavedChatMessageStateJpa.valueOf(scms.name()))
                .orElse(null);
    }
}