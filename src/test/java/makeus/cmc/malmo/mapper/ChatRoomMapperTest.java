package makeus.cmc.malmo.mapper;

import makeus.cmc.malmo.adaptor.out.persistence.entity.chat.ChatRoomEntity;
import makeus.cmc.malmo.adaptor.out.persistence.entity.value.MemberEntityId;
import makeus.cmc.malmo.adaptor.out.persistence.mapper.ChatRoomMapper;
import makeus.cmc.malmo.domain.model.chat.ChatRoom;
import makeus.cmc.malmo.domain.value.id.MemberId;
import makeus.cmc.malmo.domain.value.state.ChatRoomState;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
@DisplayName("ChatRoomMapper 테스트")
class ChatRoomMapperTest {

    @InjectMocks
    private ChatRoomMapper chatRoomMapper;

    @Test
    @DisplayName("ChatRoomEntity를 ChatRoom Domain으로 변환한다")
    void toDomain() {
        // given
        LocalDateTime now = LocalDateTime.now();
        ChatRoomEntity entity = ChatRoomEntity.builder()
                .id(1L)
                .memberEntityId(MemberEntityId.of(100L))
                .chatRoomState(ChatRoomState.ALIVE)
                .level(1)
                .lastMessageSentTime(now)
                .totalSummary("total summary")
                .situationKeyword("situation")
                .solutionKeyword("solution")
                .createdAt(now)
                .modifiedAt(now)
                .deletedAt(null)
                .build();

        // when
        ChatRoom domain = chatRoomMapper.toDomain(entity);

        // then
        assertThat(domain.getId()).isEqualTo(entity.getId());
        assertThat(domain.getMemberId().getValue()).isEqualTo(entity.getMemberEntityId().getValue());
        assertThat(domain.getChatRoomState()).isEqualTo(entity.getChatRoomState());
        assertThat(domain.getLevel()).isEqualTo(entity.getLevel());
        assertThat(domain.getLastMessageSentTime()).isEqualTo(entity.getLastMessageSentTime());
        assertThat(domain.getTotalSummary()).isEqualTo(entity.getTotalSummary());
        assertThat(domain.getSituationKeyword()).isEqualTo(entity.getSituationKeyword());
        assertThat(domain.getSolutionKeyword()).isEqualTo(entity.getSolutionKeyword());
        assertThat(domain.getCreatedAt()).isEqualTo(entity.getCreatedAt());
        assertThat(domain.getModifiedAt()).isEqualTo(entity.getModifiedAt());
        assertThat(domain.getDeletedAt()).isEqualTo(entity.getDeletedAt());
    }

    @Test
    @DisplayName("ChatRoom Domain을 ChatRoomEntity로 변환한다")
    void toEntity() {
        // given
        LocalDateTime now = LocalDateTime.now();
        ChatRoom domain = ChatRoom.from(
                1L,
                MemberId.of(100L),
                ChatRoomState.ALIVE,
                1,
                now,
                "total summary",
                "situation",
                "solution",
                null,
                null,
                now,
                now,
                null
        );

        // when
        ChatRoomEntity entity = chatRoomMapper.toEntity(domain);

        // then
        assertThat(entity.getId()).isEqualTo(domain.getId());
        assertThat(entity.getMemberEntityId().getValue()).isEqualTo(domain.getMemberId().getValue());
        assertThat(entity.getChatRoomState()).isEqualTo(domain.getChatRoomState());
        assertThat(entity.getLevel()).isEqualTo(domain.getLevel());
        assertThat(entity.getLastMessageSentTime()).isEqualTo(domain.getLastMessageSentTime());
        assertThat(entity.getTotalSummary()).isEqualTo(domain.getTotalSummary());
        assertThat(entity.getSituationKeyword()).isEqualTo(domain.getSituationKeyword());
        assertThat(entity.getSolutionKeyword()).isEqualTo(domain.getSolutionKeyword());
        assertThat(entity.getCreatedAt()).isEqualTo(domain.getCreatedAt());
        assertThat(entity.getModifiedAt()).isEqualTo(domain.getModifiedAt());
        assertThat(entity.getDeletedAt()).isEqualTo(domain.getDeletedAt());
    }

    @Test
    @DisplayName("null Domain을 Entity로 변환하면 null을 반환한다")
    void toEntity_withNullDomain_shouldReturnNull() {
        // when
        ChatRoomEntity entity = chatRoomMapper.toEntity(null);

        // then
        assertThat(entity).isNull();
    }
}