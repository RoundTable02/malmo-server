package makeus.cmc.malmo.mapper;

import makeus.cmc.malmo.adaptor.out.persistence.entity.love_type.LoveTypeCategoryJpa;
import makeus.cmc.malmo.adaptor.out.persistence.entity.love_type.LoveTypeEntity;
import makeus.cmc.malmo.adaptor.out.persistence.mapper.LoveTypeMapper;
import makeus.cmc.malmo.domain.model.love_type.LoveType;
import makeus.cmc.malmo.domain.model.love_type.LoveTypeCategory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
@DisplayName("LoveTypeMapper 테스트")
class LoveTypeMapperTest {

    @InjectMocks
    private LoveTypeMapper loveTypeMapper;

    @Nested
    @DisplayName("Entity를 Domain으로 변환할 때")
    class ToDomainTest {

        @Test
        @DisplayName("모든 필드가 있는 LoveTypeEntity를 LoveType으로 변환한다")
        void givenCompleteEntity_whenToDomain_thenReturnsCompleteLoveType() {
            // given
            LoveTypeEntity entity = createCompleteEntity();

            // when
            LoveType result = loveTypeMapper.toDomain(entity);

            // then
            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(1L);
            assertThat(result.getTitle()).isEqualTo("안정형");
            assertThat(result.getContent()).isEqualTo("안정적인 애착 스타일입니다.");
            assertThat(result.getImageUrl()).isEqualTo("http://example.com/secure.jpg");
            assertThat(result.getLoveTypeCategory()).isEqualTo(LoveTypeCategory.STABLE_TYPE);
            assertThat(result.getCreatedAt()).isNotNull();
            assertThat(result.getModifiedAt()).isNotNull();
            assertThat(result.getDeletedAt()).isNull();
        }

        @Test
        @DisplayName("null Entity가 주어지면 null을 반환한다")
        void givenNullEntity_whenToDomain_thenReturnsNull() {
            // given
            LoveTypeEntity entity = null;

            // when
            LoveType result = loveTypeMapper.toDomain(entity);

            // then
            assertThat(result).isNull();
        }

        @Test
        @DisplayName("일부 필드가 null인 Entity를 변환한다")
        void givenEntityWithNullFields_whenToDomain_thenReturnsLoveTypeWithNullFields() {
            // given
            LoveTypeEntity entity = createEntityWithNullFields();

            // when
            LoveType result = loveTypeMapper.toDomain(entity);

            // then
            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(1L);
            assertThat(result.getTitle()).isNull();
            assertThat(result.getContent()).isNull();
            assertThat(result.getImageUrl()).isNull();
            assertThat(result.getLoveTypeCategory()).isNull();
        }

        @Test
        @DisplayName("삭제된 LoveTypeEntity를 변환한다")
        void givenDeletedEntity_whenToDomain_thenReturnsDeletedLoveType() {
            // given
            LocalDateTime deletedAt = LocalDateTime.now();
            LoveTypeEntity entity = createDeletedEntity(deletedAt);

            // when
            LoveType result = loveTypeMapper.toDomain(entity);

            // then
            assertThat(result).isNotNull();
            assertThat(result.getDeletedAt()).isEqualTo(deletedAt);
        }
    }

    @Nested
    @DisplayName("Domain을 Entity로 변환할 때")
    class ToEntityTest {

        @Test
        @DisplayName("모든 필드가 있는 LoveType을 LoveTypeEntity로 변환한다")
        void givenCompleteLoveType_whenToEntity_thenReturnsCompleteEntity() {
            // given
            LoveType domain = createCompleteLoveType();

            // when
            LoveTypeEntity result = loveTypeMapper.toEntity(domain);

            // then
            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(1L);
            assertThat(result.getTitle()).isEqualTo("안정형");
            assertThat(result.getContent()).isEqualTo("안정적인 애착 스타일입니다.");
            assertThat(result.getImageUrl()).isEqualTo("http://example.com/secure.jpg");
            assertThat(result.getLoveTypeCategoryJpa()).isEqualTo(LoveTypeCategoryJpa.STABLE_TYPE);
        }

        @Test
        @DisplayName("null Domain이 주어지면 null을 반환한다")
        void givenNullDomain_whenToEntity_thenReturnsNull() {
            // given
            LoveType domain = null;

            // when
            LoveTypeEntity result = loveTypeMapper.toEntity(domain);

            // then
            assertThat(result).isNull();
        }

        @Test
        @DisplayName("일부 필드가 null인 LoveType을 변환한다")
        void givenLoveTypeWithNullFields_whenToEntity_thenReturnsEntityWithNullFields() {
            // given
            LoveType domain = createLoveTypeWithNullFields();

            // when
            LoveTypeEntity result = loveTypeMapper.toEntity(domain);

            // then
            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(1L);
            assertThat(result.getTitle()).isNull();
            assertThat(result.getContent()).isNull();
            assertThat(result.getImageUrl()).isNull();
            assertThat(result.getLoveTypeCategoryJpa()).isNull();
        }

        @Test
        @DisplayName("null ID를 가진 LoveType을 변환한다")
        void givenLoveTypeWithNullId_whenToEntity_thenReturnsEntityWithNullId() {
            // given
            LoveType domain = createLoveTypeWithNullId();

            // when
            LoveTypeEntity result = loveTypeMapper.toEntity(domain);

            // then
            assertThat(result).isNotNull();
            assertThat(result.getId()).isNull();
            assertThat(result.getTitle()).isEqualTo("회피형");
        }

    }

    // Test data creation methods
    private LoveTypeEntity createCompleteEntity() {
        return LoveTypeEntity.builder()
                .id(1L)
                .title("안정형")
                .content("안정적인 애착 스타일입니다.")
                .imageUrl("http://example.com/secure.jpg")
                .loveTypeCategoryJpa(LoveTypeCategoryJpa.STABLE_TYPE)
                .createdAt(LocalDateTime.now())
                .modifiedAt(LocalDateTime.now())
                .deletedAt(null)
                .build();
    }

    private LoveTypeEntity createEntityWithNullFields() {
        return LoveTypeEntity.builder()
                .id(1L)
                .title(null)
                .content(null)
                .imageUrl(null)
                .loveTypeCategoryJpa(null)
                .build();
    }

    private LoveTypeEntity createDeletedEntity(LocalDateTime deletedAt) {
        return LoveTypeEntity.builder()
                .id(1L)
                .title("불안정형")
                .content("불안정한 애착 스타일입니다.")
                .imageUrl("http://example.com/anxious.jpg")
                .loveTypeCategoryJpa(LoveTypeCategoryJpa.STABLE_TYPE)
                .deletedAt(deletedAt)
                .build();
    }

    private LoveType createCompleteLoveType() {
        return LoveType.builder()
                .id(1L)
                .title("안정형")
                .content("안정적인 애착 스타일입니다.")
                .imageUrl("http://example.com/secure.jpg")
                .loveTypeCategory(LoveTypeCategory.STABLE_TYPE)
                .createdAt(LocalDateTime.now())
                .modifiedAt(LocalDateTime.now())
                .deletedAt(null)
                .build();
    }

    private LoveType createLoveTypeWithNullFields() {
        return LoveType.builder()
                .id(1L)
                .title(null)
                .content(null)
                .imageUrl(null)
                .loveTypeCategory(null)
                .build();
    }

    private LoveType createLoveTypeWithNullId() {
        return LoveType.builder()
                .id(null)
                .title("회피형")
                .content("회피적인 애착 스타일입니다.")
                .imageUrl("http://example.com/avoidant.jpg")
                .loveTypeCategory(LoveTypeCategory.STABLE_TYPE)
                .build();
    }
}