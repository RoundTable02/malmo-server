package makeus.cmc.malmo.mapper;

import makeus.cmc.malmo.adaptor.out.persistence.entity.chat.ChatMessageEntity;
import makeus.cmc.malmo.adaptor.out.persistence.entity.value.ChatRoomEntityId;
import makeus.cmc.malmo.adaptor.out.persistence.mapper.ChatMessageMapper;
import makeus.cmc.malmo.domain.model.chat.ChatMessage;
import makeus.cmc.malmo.domain.value.id.ChatRoomId;
import makeus.cmc.malmo.domain.value.type.SenderType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
@DisplayName("ChatMessageMapper 테스트")
class ChatMessageMapperTest {

    @InjectMocks
    private ChatMessageMapper chatMessageMapper;

    @Test
    @DisplayName("ChatMessageEntity를 ChatMessage Domain으로 변환한다")
    void toDomain() {
        // given
        LocalDateTime now = LocalDateTime.now();
        ChatMessageEntity entity = ChatMessageEntity.builder()
                .id(1L)
                .chatRoomEntityId(ChatRoomEntityId.of(100L))
                .level(1)
                .detailedLevel(2)
                .content("test content")
                .senderType(SenderType.USER)
                .createdAt(now)
                .modifiedAt(now)
                .deletedAt(null)
                .build();

        // when
        ChatMessage domain = chatMessageMapper.toDomain(entity);

        // then
        assertThat(domain.getId()).isEqualTo(entity.getId());
        assertThat(domain.getChatRoomId().getValue()).isEqualTo(entity.getChatRoomEntityId().getValue());
        assertThat(domain.getLevel()).isEqualTo(entity.getLevel());
        assertThat(domain.getDetailedLevel()).isEqualTo(entity.getDetailedLevel());
        assertThat(domain.getContent()).isEqualTo(entity.getContent());
        assertThat(domain.getSenderType()).isEqualTo(entity.getSenderType());
        assertThat(domain.getCreatedAt()).isEqualTo(entity.getCreatedAt());
        assertThat(domain.getModifiedAt()).isEqualTo(entity.getModifiedAt());
        assertThat(domain.getDeletedAt()).isEqualTo(entity.getDeletedAt());
    }

    @Test
    @DisplayName("ChatMessage Domain을 ChatMessageEntity로 변환한다")
    void toEntity() {
        // given
        LocalDateTime now = LocalDateTime.now();
        ChatMessage domain = ChatMessage.from(
                1L,
                ChatRoomId.of(100L),
                1,
                2,
                "test content",
                SenderType.USER,
                now,
                now,
                null
        );

        // when
        ChatMessageEntity entity = chatMessageMapper.toEntity(domain);

        // then
        assertThat(entity.getId()).isEqualTo(domain.getId());
        assertThat(entity.getChatRoomEntityId().getValue()).isEqualTo(domain.getChatRoomId().getValue());
        assertThat(entity.getLevel()).isEqualTo(domain.getLevel());
        assertThat(entity.getDetailedLevel()).isEqualTo(domain.getDetailedLevel());
        assertThat(entity.getContent()).isEqualTo(domain.getContent());
        assertThat(entity.getSenderType()).isEqualTo(domain.getSenderType());
        assertThat(entity.getCreatedAt()).isEqualTo(domain.getCreatedAt());
        assertThat(entity.getModifiedAt()).isEqualTo(domain.getModifiedAt());
        assertThat(entity.getDeletedAt()).isEqualTo(domain.getDeletedAt());
    }

    @Test
    @DisplayName("null ChatMessage Domain을 Entity로 변환하면 null을 반환한다")
    void toEntity_withNullDomain_shouldReturnNull() {
        // when
        ChatMessageEntity entity = chatMessageMapper.toEntity(null);

        // then
        assertThat(entity).isNull();
    }
}