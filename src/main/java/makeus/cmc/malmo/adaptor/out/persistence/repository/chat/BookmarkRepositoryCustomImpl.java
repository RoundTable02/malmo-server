package makeus.cmc.malmo.adaptor.out.persistence.repository.chat;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import makeus.cmc.malmo.application.port.out.chat.LoadBookmarkPort;
import makeus.cmc.malmo.domain.value.state.BookmarkState;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

import static makeus.cmc.malmo.adaptor.out.persistence.entity.chat.QBookmarkEntity.bookmarkEntity;
import static makeus.cmc.malmo.adaptor.out.persistence.entity.chat.QChatMessageEntity.chatMessageEntity;

@RequiredArgsConstructor
public class BookmarkRepositoryCustomImpl implements BookmarkRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<LoadBookmarkPort.BookmarkDto> loadBookmarksByMemberAndChatRoom(
            Long memberId, Long chatRoomId, Pageable pageable) {

        List<LoadBookmarkPort.BookmarkDto> content = queryFactory
                .select(Projections.constructor(LoadBookmarkPort.BookmarkDto.class,
                        bookmarkEntity.id,
                        chatMessageEntity.id,
                        chatMessageEntity.content,
                        chatMessageEntity.senderType,
                        chatMessageEntity.createdAt))
                .from(bookmarkEntity)
                .join(chatMessageEntity).on(bookmarkEntity.chatMessageEntityId.value.eq(chatMessageEntity.id))
                .where(bookmarkEntity.memberEntityId.value.eq(memberId)
                        .and(bookmarkEntity.chatRoomEntityId.value.eq(chatRoomId))
                        .and(bookmarkEntity.bookmarkState.eq(BookmarkState.ALIVE)))
                .orderBy(bookmarkEntity.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long total = queryFactory
                .select(bookmarkEntity.count())
                .from(bookmarkEntity)
                .where(bookmarkEntity.memberEntityId.value.eq(memberId)
                        .and(bookmarkEntity.chatRoomEntityId.value.eq(chatRoomId))
                        .and(bookmarkEntity.bookmarkState.eq(BookmarkState.ALIVE)))
                .fetchOne();

        return new PageImpl<>(content, pageable, total != null ? total : 0L);
    }

    @Override
    public boolean isMemberOwnerOfBookmarks(Long memberId, List<Long> bookmarkIds) {
        Long count = queryFactory
                .select(bookmarkEntity.count())
                .from(bookmarkEntity)
                .where(bookmarkEntity.id.in(bookmarkIds)
                        .and(bookmarkEntity.memberEntityId.value.eq(memberId))
                        .and(bookmarkEntity.bookmarkState.eq(BookmarkState.ALIVE)))
                .fetchOne();

        return count != null && count == bookmarkIds.size();
    }

    @Override
    public void softDeleteBookmarks(List<Long> bookmarkIds) {
        queryFactory
                .update(bookmarkEntity)
                .set(bookmarkEntity.bookmarkState, BookmarkState.DELETED)
                .set(bookmarkEntity.deletedAt, LocalDateTime.now())
                .where(bookmarkEntity.id.in(bookmarkIds))
                .execute();
    }

    @Override
    public long countMessagesBeforeId(Long chatRoomId, Long messageId, String sort) {
        Long count;
        if ("ASC".equalsIgnoreCase(sort)) {
            count = queryFactory
                    .select(chatMessageEntity.count())
                    .from(chatMessageEntity)
                    .where(chatMessageEntity.chatRoomEntityId.value.eq(chatRoomId)
                            .and(chatMessageEntity.id.lt(messageId)))
                    .fetchOne();
        } else {
            count = queryFactory
                    .select(chatMessageEntity.count())
                    .from(chatMessageEntity)
                    .where(chatMessageEntity.chatRoomEntityId.value.eq(chatRoomId)
                            .and(chatMessageEntity.id.gt(messageId)))
                    .fetchOne();
        }
        return count != null ? count : 0L;
    }
}
