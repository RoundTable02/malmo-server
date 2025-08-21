package makeus.cmc.malmo.mapper;

import makeus.cmc.malmo.adaptor.out.persistence.entity.question.QuestionEntity;
import makeus.cmc.malmo.adaptor.out.persistence.mapper.QuestionMapper;
import makeus.cmc.malmo.domain.model.question.Question;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
@DisplayName("QuestionMapper 테스트")
class QuestionMapperTest {

    @InjectMocks
    private QuestionMapper questionMapper;

    @Test
    @DisplayName("Entity를 Domain으로 변환한다")
    void toDomain() {
        // given
        LocalDateTime now = LocalDateTime.now();
        QuestionEntity entity = QuestionEntity.builder()
                .id(1L)
                .title("title")
                .content("content")
                .level(1)
                .createdAt(now)
                .modifiedAt(now)
                .deletedAt(null)
                .build();

        // when
        Question domain = questionMapper.toDomain(entity);

        // then
        assertThat(domain.getId()).isEqualTo(entity.getId());
        assertThat(domain.getTitle()).isEqualTo(entity.getTitle());
        assertThat(domain.getContent()).isEqualTo(entity.getContent());
        assertThat(domain.getLevel()).isEqualTo(entity.getLevel());
        assertThat(domain.getCreatedAt()).isEqualTo(entity.getCreatedAt());
        assertThat(domain.getModifiedAt()).isEqualTo(entity.getModifiedAt());
        assertThat(domain.getDeletedAt()).isEqualTo(entity.getDeletedAt());
    }

    @Test
    @DisplayName("Domain을 Entity로 변환한다")
    void toEntity() {
        // given
        LocalDateTime now = LocalDateTime.now();
        Question domain = Question.from(
                1L,
                "title",
                "content",
                1,
                now,
                now,
                null
        );

        // when
        QuestionEntity entity = questionMapper.toEntity(domain);

        // then
        assertThat(entity.getId()).isEqualTo(domain.getId());
        assertThat(entity.getTitle()).isEqualTo(domain.getTitle());
        assertThat(entity.getContent()).isEqualTo(domain.getContent());
        assertThat(entity.getLevel()).isEqualTo(domain.getLevel());
        assertThat(entity.getCreatedAt()).isEqualTo(domain.getCreatedAt());
        assertThat(entity.getModifiedAt()).isEqualTo(domain.getModifiedAt());
        assertThat(entity.getDeletedAt()).isEqualTo(domain.getDeletedAt());
    }

    @Test
    @DisplayName("null Entity를 Domain으로 변환하면 null을 반환한다")
    void toDomain_withNullEntity() {
        // when
        Question domain = questionMapper.toDomain(null);

        // then
        assertThat(domain).isNull();
    }

    @Test
    @DisplayName("null Domain을 Entity로 변환하면 null을 반환한다")
    void toEntity_withNullDomain() {
        // when
        QuestionEntity entity = questionMapper.toEntity(null);

        // then
        assertThat(entity).isNull();
    }
}