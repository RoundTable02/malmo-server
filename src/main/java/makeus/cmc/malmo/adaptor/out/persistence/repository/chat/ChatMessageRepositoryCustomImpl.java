package makeus.cmc.malmo.adaptor.out.persistence.repository.chat;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import makeus.cmc.malmo.application.port.out.chat.LoadMessagesPort;
import makeus.cmc.malmo.domain.value.state.SavedChatMessageState;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static makeus.cmc.malmo.adaptor.out.persistence.entity.chat.QChatMessageEntity.chatMessageEntity;
import static makeus.cmc.malmo.adaptor.out.persistence.entity.chat.QSavedChatMessageEntity.savedChatMessageEntity;

@RequiredArgsConstructor
public class ChatMessageRepositoryCustomImpl implements ChatMessageRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<LoadMessagesPort.ChatRoomMessageRepositoryDto> loadCurrentMessagesDto(Long chatRoomId, Pageable pageable) {
        List<LoadMessagesPort.ChatRoomMessageRepositoryDto> content = queryFactory.select(Projections.constructor(LoadMessagesPort.ChatRoomMessageRepositoryDto.class,
                        chatMessageEntity.id,
                        chatMessageEntity.senderType,
                        chatMessageEntity.content,
                        chatMessageEntity.createdAt,
                        savedChatMessageEntity.isNotNull()
                ))
                .from(chatMessageEntity)
                .leftJoin(savedChatMessageEntity)
                .on(savedChatMessageEntity.chatMessageEntityId.value.eq(chatMessageEntity.id)
                        .and(savedChatMessageEntity.savedChatMessageState.eq(SavedChatMessageState.ALIVE)))
                .where(chatMessageEntity.chatRoomEntityId.value.eq(chatRoomId))
                .orderBy(chatMessageEntity.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        long total = queryFactory.select(chatMessageEntity.count())
                .from(chatMessageEntity)
                .leftJoin(savedChatMessageEntity)
                .on(savedChatMessageEntity.chatMessageEntityId.value.eq(chatMessageEntity.id)
                        .and(savedChatMessageEntity.savedChatMessageState.eq(SavedChatMessageState.ALIVE)))
                .where(chatMessageEntity.chatRoomEntityId.value.eq(chatRoomId))
                .fetchOne();

        return new PageImpl<>(content, pageable, total);
    }

    @Override
    public Page<LoadMessagesPort.ChatRoomMessageRepositoryDto> loadCurrentMessagesDtoAsc(Long chatRoomId, Pageable pageable) {
        List<LoadMessagesPort.ChatRoomMessageRepositoryDto> content = queryFactory.select(Projections.constructor(LoadMessagesPort.ChatRoomMessageRepositoryDto.class,
                        chatMessageEntity.id,
                        chatMessageEntity.senderType,
                        chatMessageEntity.content,
                        chatMessageEntity.createdAt,
                        savedChatMessageEntity.isNotNull()
                ))
                .from(chatMessageEntity)
                .leftJoin(savedChatMessageEntity)
                .on(savedChatMessageEntity.chatMessageEntityId.value.eq(chatMessageEntity.id)
                        .and(savedChatMessageEntity.savedChatMessageState.eq(SavedChatMessageState.ALIVE)))
                .where(chatMessageEntity.chatRoomEntityId.value.eq(chatRoomId))
                .orderBy(chatMessageEntity.createdAt.asc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        long total = queryFactory.select(chatMessageEntity.count())
                .from(chatMessageEntity)
                .leftJoin(savedChatMessageEntity)
                .on(savedChatMessageEntity.chatMessageEntityId.value.eq(chatMessageEntity.id)
                        .and(savedChatMessageEntity.savedChatMessageState.eq(SavedChatMessageState.ALIVE)))
                .where(chatMessageEntity.chatRoomEntityId.value.eq(chatRoomId))
                .fetchOne();

        return new PageImpl<>(content, pageable, total);
    }
}
