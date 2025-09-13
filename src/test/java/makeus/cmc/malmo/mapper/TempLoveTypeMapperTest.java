package makeus.cmc.malmo.mapper;

import makeus.cmc.malmo.adaptor.out.persistence.entity.TempLoveTypeEntity;
import makeus.cmc.malmo.adaptor.out.persistence.mapper.TempLoveTypeMapper;
import makeus.cmc.malmo.domain.model.love_type.TempLoveType;
import makeus.cmc.malmo.domain.value.type.LoveTypeCategory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
@DisplayName("TempLoveTypeMapper 테스트")
class TempLoveTypeMapperTest {

    @InjectMocks
    private TempLoveTypeMapper tempLoveTypeMapper;

    @Test
    @DisplayName("Entity를 Domain으로 변환한다")
    void toDomain() {
        // given
        LocalDateTime now = LocalDateTime.now();
        TempLoveTypeEntity entity = TempLoveTypeEntity.builder()
                .id(1L)
                .category(LoveTypeCategory.STABLE_TYPE)
                .avoidanceRate(10.5f)
                .anxietyRate(20.5f)
                .createdAt(now)
                .modifiedAt(now)
                .deletedAt(null)
                .build();

        // when
        TempLoveType domain = tempLoveTypeMapper.toDomain(entity);

        // then
        assertThat(domain.getId()).isEqualTo(entity.getId());
        assertThat(domain.getCategory()).isEqualTo(entity.getCategory());
        assertThat(domain.getAvoidanceRate()).isEqualTo(entity.getAvoidanceRate());
        assertThat(domain.getAnxietyRate()).isEqualTo(entity.getAnxietyRate());
        assertThat(domain.getCreatedAt()).isEqualTo(entity.getCreatedAt());
        assertThat(domain.getModifiedAt()).isEqualTo(entity.getModifiedAt());
        assertThat(domain.getDeletedAt()).isEqualTo(entity.getDeletedAt());
    }

    @Test
    @DisplayName("Domain을 Entity로 변환한다")
    void toEntity() {
        // given
        LocalDateTime now = LocalDateTime.now();
        TempLoveType domain = TempLoveType.from(
                1L,
                LoveTypeCategory.STABLE_TYPE,
                10.5f,
                20.5f,
                now,
                now,
                null
        );

        // when
        TempLoveTypeEntity entity = tempLoveTypeMapper.toEntity(domain);

        // then
        assertThat(entity.getId()).isEqualTo(domain.getId());
        assertThat(entity.getCategory()).isEqualTo(domain.getCategory());
        assertThat(entity.getAvoidanceRate()).isEqualTo(domain.getAvoidanceRate());
        assertThat(entity.getAnxietyRate()).isEqualTo(domain.getAnxietyRate());
        assertThat(entity.getCreatedAt()).isEqualTo(domain.getCreatedAt());
        assertThat(entity.getModifiedAt()).isEqualTo(domain.getModifiedAt());
        assertThat(entity.getDeletedAt()).isEqualTo(domain.getDeletedAt());
    }

    @Test
    @DisplayName("null Entity를 Domain으로 변환하면 null을 반환한다")
    void toDomain_withNullEntity() {
        // when
        TempLoveType domain = tempLoveTypeMapper.toDomain(null);

        // then
        assertThat(domain).isNull();
    }

    @Test
    @DisplayName("null Domain을 Entity로 변환하면 null을 반환한다")
    void toEntity_withNullDomain() {
        // when
        TempLoveTypeEntity entity = tempLoveTypeMapper.toEntity(null);

        // then
        assertThat(entity).isNull();
    }
}