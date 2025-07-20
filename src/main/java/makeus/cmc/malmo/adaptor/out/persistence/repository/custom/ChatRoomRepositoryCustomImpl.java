package makeus.cmc.malmo.adaptor.out.persistence.repository.custom;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import makeus.cmc.malmo.adaptor.out.persistence.entity.chat.ChatRoomEntity;
import makeus.cmc.malmo.domain.value.state.ChatRoomState;

import java.util.List;

import static makeus.cmc.malmo.adaptor.out.persistence.entity.chat.QChatRoomEntity.chatRoomEntity;

@RequiredArgsConstructor
public class ChatRoomRepositoryCustomImpl implements ChatRoomRepositoryCustom{

    private final JPAQueryFactory queryFactory;

    @Override
    public List<ChatRoomEntity> loadChatRoomListByMemberId(Long memberId, String keyword, int page, int size) {
        BooleanExpression keywordCondition = keyword == null || keyword.isEmpty()
                ? null
                : chatRoomEntity.totalSummary.containsIgnoreCase(keyword);

        return queryFactory.selectFrom(chatRoomEntity)
                .where(chatRoomEntity.memberEntityId.value.eq(memberId)
                        .and(chatRoomEntity.chatRoomState.eq(ChatRoomState.COMPLETED))
                        .and(keywordCondition))
                .orderBy(chatRoomEntity.createdAt.desc())
                .offset((long) page * size)
                .limit(size)
                .fetch();
    }

    @Override
    public boolean isMemberOwnerOfChatRooms(Long memberId, List<Long> chatRoomIds) {
        Long count = queryFactory.select(chatRoomEntity.count())
                .from(chatRoomEntity)
                .where(chatRoomEntity.memberEntityId.value.eq(memberId)
                        .and(chatRoomEntity.chatRoomState.eq(ChatRoomState.COMPLETED))
                        .and(chatRoomEntity.id.in(chatRoomIds)))
                .fetchOne();

        return count != null && count == chatRoomIds.size();
    }

    @Override
    public void deleteChatRooms(List<Long> chatRoomIds) {
        queryFactory.update(chatRoomEntity)
                .set(chatRoomEntity.chatRoomState, ChatRoomState.DELETED)
                .where(chatRoomEntity.id.in(chatRoomIds))
                .execute();
    }
}
