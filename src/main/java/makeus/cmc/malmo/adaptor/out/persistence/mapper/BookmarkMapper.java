package makeus.cmc.malmo.adaptor.out.persistence.mapper;

import makeus.cmc.malmo.adaptor.out.persistence.entity.chat.BookmarkEntity;
import makeus.cmc.malmo.adaptor.out.persistence.entity.value.ChatMessageEntityId;
import makeus.cmc.malmo.adaptor.out.persistence.entity.value.ChatRoomEntityId;
import makeus.cmc.malmo.adaptor.out.persistence.entity.value.MemberEntityId;
import makeus.cmc.malmo.domain.model.chat.Bookmark;
import makeus.cmc.malmo.domain.value.id.ChatRoomId;
import makeus.cmc.malmo.domain.value.id.MemberId;
import org.springframework.stereotype.Component;

@Component
public class BookmarkMapper {

    public Bookmark toDomain(BookmarkEntity entity) {
        return Bookmark.from(
                entity.getId(),
                entity.getChatRoomEntityId() != null
                        ? ChatRoomId.of(entity.getChatRoomEntityId().getValue()) : null,
                entity.getChatMessageEntityId() != null
                        ? entity.getChatMessageEntityId().getValue() : null,
                entity.getMemberEntityId() != null
                        ? MemberId.of(entity.getMemberEntityId().getValue()) : null,
                entity.getBookmarkState(),
                entity.getCreatedAt(),
                entity.getModifiedAt(),
                entity.getDeletedAt()
        );
    }

    public BookmarkEntity toEntity(Bookmark domain) {
        return BookmarkEntity.builder()
                .id(domain.getId())
                .chatRoomEntityId(domain.getChatRoomId() != null
                        ? ChatRoomEntityId.of(domain.getChatRoomId().getValue()) : null)
                .chatMessageEntityId(domain.getChatMessageId() != null
                        ? ChatMessageEntityId.of(domain.getChatMessageId()) : null)
                .memberEntityId(domain.getMemberId() != null
                        ? MemberEntityId.of(domain.getMemberId().getValue()) : null)
                .bookmarkState(domain.getBookmarkState())
                .createdAt(domain.getCreatedAt())
                .modifiedAt(domain.getModifiedAt())
                .deletedAt(domain.getDeletedAt())
                .build();
    }
}
