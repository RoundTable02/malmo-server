package makeus.cmc.malmo.mapper;

import makeus.cmc.malmo.adaptor.out.persistence.entity.chat.ChatMessageSummaryEntity;
import makeus.cmc.malmo.adaptor.out.persistence.entity.value.ChatRoomEntityId;
import makeus.cmc.malmo.adaptor.out.persistence.mapper.ChatMessageSummaryMapper;
import makeus.cmc.malmo.domain.model.chat.ChatMessageSummary;
import makeus.cmc.malmo.domain.value.id.ChatRoomId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
@DisplayName("ChatMessageSummaryMapper 테스트")
class ChatMessageSummaryMapperTest {

    @InjectMocks
    private ChatMessageSummaryMapper chatMessageSummaryMapper;

    @Test
    @DisplayName("ChatMessageSummaryEntity를 ChatMessageSummary Domain으로 변환한다")
    void toDomain() {
        // given
        LocalDateTime now = LocalDateTime.now();
        ChatMessageSummaryEntity entity = ChatMessageSummaryEntity.builder()
                .id(1L)
                .chatRoomEntityId(ChatRoomEntityId.of(100L))
                .content("summary content")
                .level(1)
                .createdAt(now)
                .modifiedAt(now)
                .deletedAt(null)
                .build();

        // when
        ChatMessageSummary domain = chatMessageSummaryMapper.toDomain(entity);

        // then
        assertThat(domain.getId()).isEqualTo(entity.getId());
        assertThat(domain.getChatRoomId().getValue()).isEqualTo(entity.getChatRoomEntityId().getValue());
        assertThat(domain.getContent()).isEqualTo(entity.getContent());
        assertThat(domain.getLevel()).isEqualTo(entity.getLevel());
        assertThat(domain.getCreatedAt()).isEqualTo(entity.getCreatedAt());
        assertThat(domain.getModifiedAt()).isEqualTo(entity.getModifiedAt());
        assertThat(domain.getDeletedAt()).isEqualTo(entity.getDeletedAt());
    }

    @Test
    @DisplayName("ChatMessageSummary Domain을 ChatMessageSummaryEntity로 변환한다")
    void toEntity() {
        // given
        LocalDateTime now = LocalDateTime.now();
        ChatMessageSummary domain = ChatMessageSummary.from(
                1L,
                ChatRoomId.of(100L),
                "summary content",
                1,
                now,
                now,
                null
        );

        // when
        ChatMessageSummaryEntity entity = chatMessageSummaryMapper.toEntity(domain);

        // then
        assertThat(entity.getId()).isEqualTo(domain.getId());
        assertThat(entity.getChatRoomEntityId().getValue()).isEqualTo(domain.getChatRoomId().getValue());
        assertThat(entity.getContent()).isEqualTo(domain.getContent());
        assertThat(entity.getLevel()).isEqualTo(domain.getLevel());
        assertThat(entity.getCreatedAt()).isEqualTo(domain.getCreatedAt());
        assertThat(entity.getModifiedAt()).isEqualTo(domain.getModifiedAt());
        assertThat(entity.getDeletedAt()).isEqualTo(domain.getDeletedAt());
    }

    @Test
    @DisplayName("null Entity를 Domain으로 변환하면 null을 반환한다")
    void toDomain_withNullEntity_shouldReturnNull() {
        // when
        ChatMessageSummary domain = chatMessageSummaryMapper.toDomain(null);

        // then
        assertThat(domain).isNull();
    }

    @Test
    @DisplayName("null Domain을 Entity로 변환하면 null을 반환한다")
    void toEntity_withNullDomain_shouldReturnNull() {
        // when
        ChatMessageSummaryEntity entity = chatMessageSummaryMapper.toEntity(null);

        // then
        assertThat(entity).isNull();
    }
}