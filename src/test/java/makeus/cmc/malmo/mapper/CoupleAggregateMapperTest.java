package makeus.cmc.malmo.mapper;

import makeus.cmc.malmo.adaptor.out.persistence.entity.couple.CoupleEntity;
import makeus.cmc.malmo.adaptor.out.persistence.entity.couple.CoupleMemberSnapshotEntity;
import makeus.cmc.malmo.adaptor.out.persistence.entity.value.MemberEntityId;
import makeus.cmc.malmo.adaptor.out.persistence.mapper.CoupleAggregateMapper;
import makeus.cmc.malmo.domain.model.couple.Couple;
import makeus.cmc.malmo.domain.model.couple.CoupleMemberSnapshot;
import makeus.cmc.malmo.domain.value.id.MemberId;
import makeus.cmc.malmo.domain.value.state.CoupleState;
import makeus.cmc.malmo.domain.value.type.LoveTypeCategory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
@DisplayName("CoupleAggregateMapper 테스트")
class CoupleAggregateMapperTest {

    @InjectMocks
    private CoupleAggregateMapper coupleAggregateMapper;

    @Nested
    @DisplayName("Entity를 Domain으로 변환할 때")
    class ToDomainTest {

        @Test
        @DisplayName("모든 필드가 있는 CoupleEntity를 Couple로 변환한다")
        void givenCompleteEntity_whenToDomain_thenReturnsCompleteCouple() {
            // given
            CoupleEntity entity = createCompleteEntity();

            // when
            Couple result = coupleAggregateMapper.toDomain(entity);

            // then
            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(entity.getId());
            assertThat(result.getStartLoveDate()).isEqualTo(entity.getStartLoveDate());
            assertThat(result.getCoupleState()).isEqualTo(entity.getCoupleState());
            assertThat(result.getFirstMemberId().getValue()).isEqualTo(entity.getFirstMemberId().getValue());
            assertThat(result.getSecondMemberId().getValue()).isEqualTo(entity.getSecondMemberId().getValue());
            assertThat(result.getCreatedAt()).isEqualTo(entity.getCreatedAt());
            assertThat(result.getModifiedAt()).isEqualTo(entity.getModifiedAt());
            assertThat(result.getDeletedAt()).isEqualTo(entity.getDeletedAt());

            // First CoupleMemberSnapshot 검증
            CoupleMemberSnapshot firstSnapshot = result.getFirstMemberSnapshot();
            CoupleMemberSnapshotEntity firstSnapshotEntity = entity.getFirstMemberSnapshot();
            assertThat(firstSnapshot.getNickname()).isEqualTo(firstSnapshotEntity.getNickname());
            assertThat(firstSnapshot.getLoveTypeCategory()).isEqualTo(firstSnapshotEntity.getLoveTypeCategory());
            assertThat(firstSnapshot.getAnxietyRate()).isEqualTo(firstSnapshotEntity.getAnxietyRate());
            assertThat(firstSnapshot.getAvoidanceRate()).isEqualTo(firstSnapshotEntity.getAvoidanceRate());

            // Second CoupleMemberSnapshot 검증
            CoupleMemberSnapshot secondSnapshot = result.getSecondMemberSnapshot();
            CoupleMemberSnapshotEntity secondSnapshotEntity = entity.getSecondMemberSnapshot();
            assertThat(secondSnapshot.getNickname()).isEqualTo(secondSnapshotEntity.getNickname());
            assertThat(secondSnapshot.getLoveTypeCategory()).isEqualTo(secondSnapshotEntity.getLoveTypeCategory());
            assertThat(secondSnapshot.getAnxietyRate()).isEqualTo(secondSnapshotEntity.getAnxietyRate());
            assertThat(secondSnapshot.getAvoidanceRate()).isEqualTo(secondSnapshotEntity.getAvoidanceRate());
        }

        @Test
        @DisplayName("null Entity를 변환하면 null을 반환한다")
        void givenNullEntity_whenToDomain_thenReturnsNull() {
            // when
            Couple result = coupleAggregateMapper.toDomain(null);

            // then
            assertThat(result).isNull();
        }
    }

    @Nested
    @DisplayName("Domain을 Entity로 변환할 때")
    class ToEntityTest {

        @Test
        @DisplayName("모든 필드가 있는 Couple을 CoupleEntity로 변환한다")
        void givenCompleteCouple_whenToEntity_thenReturnsCompleteEntity() {
            // given
            Couple domain = createCompleteCouple();

            // when
            CoupleEntity result = coupleAggregateMapper.toEntity(domain);

            // then
            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(domain.getId());
            assertThat(result.getStartLoveDate()).isEqualTo(domain.getStartLoveDate());
            assertThat(result.getCoupleState()).isEqualTo(domain.getCoupleState());
            assertThat(result.getFirstMemberId().getValue()).isEqualTo(domain.getFirstMemberId().getValue());
            assertThat(result.getSecondMemberId().getValue()).isEqualTo(domain.getSecondMemberId().getValue());
            assertThat(result.getCreatedAt()).isEqualTo(domain.getCreatedAt());
            assertThat(result.getModifiedAt()).isEqualTo(domain.getModifiedAt());
            assertThat(result.getDeletedAt()).isEqualTo(domain.getDeletedAt());

            // First CoupleMemberSnapshotEntity 검증
            CoupleMemberSnapshotEntity firstSnapshotEntity = result.getFirstMemberSnapshot();
            CoupleMemberSnapshot firstSnapshot = domain.getFirstMemberSnapshot();
            assertThat(firstSnapshotEntity.getNickname()).isEqualTo(firstSnapshot.getNickname());
            assertThat(firstSnapshotEntity.getLoveTypeCategory()).isEqualTo(firstSnapshot.getLoveTypeCategory());
            assertThat(firstSnapshotEntity.getAnxietyRate()).isEqualTo(firstSnapshot.getAnxietyRate());
            assertThat(firstSnapshotEntity.getAvoidanceRate()).isEqualTo(firstSnapshot.getAvoidanceRate());

            // Second CoupleMemberSnapshotEntity 검증
            CoupleMemberSnapshotEntity secondSnapshotEntity = result.getSecondMemberSnapshot();
            CoupleMemberSnapshot secondSnapshot = domain.getSecondMemberSnapshot();
            assertThat(secondSnapshotEntity.getNickname()).isEqualTo(secondSnapshot.getNickname());
            assertThat(secondSnapshotEntity.getLoveTypeCategory()).isEqualTo(secondSnapshot.getLoveTypeCategory());
            assertThat(secondSnapshotEntity.getAnxietyRate()).isEqualTo(secondSnapshot.getAnxietyRate());
            assertThat(secondSnapshotEntity.getAvoidanceRate()).isEqualTo(secondSnapshot.getAvoidanceRate());
        }

        @Test
        @DisplayName("null Domain을 변환하면 null을 반환한다")
        void givenNullDomain_whenToEntity_thenReturnsNull() {
            // when
            CoupleEntity result = coupleAggregateMapper.toEntity(null);

            // then
            assertThat(result).isNull();
        }
    }

    // Test data creation methods for Entity
    private CoupleEntity createCompleteEntity() {
        return CoupleEntity.builder()
                .id(1L)
                .startLoveDate(LocalDate.of(2024, 1, 1))
                .coupleState(CoupleState.ALIVE)
                .firstMemberId(MemberEntityId.of(100L))
                .secondMemberId(MemberEntityId.of(200L))
                .firstMemberSnapshot(createSnapshotEntity("first", LoveTypeCategory.STABLE_TYPE, 10, 20))
                .secondMemberSnapshot(createSnapshotEntity("second", LoveTypeCategory.ANXIETY_TYPE, 30, 40))
                .createdAt(LocalDateTime.now())
                .modifiedAt(LocalDateTime.now())
                .deletedAt(null)
                .build();
    }

    private CoupleMemberSnapshotEntity createSnapshotEntity(String nickname, LoveTypeCategory category, int anxiety, int avoidance) {
        return CoupleMemberSnapshotEntity.builder()
                .nickname(nickname)
                .loveTypeCategory(category)
                .anxietyRate(anxiety)
                .avoidanceRate(avoidance)
                .build();
    }

    // Test data creation methods for Domain
    private Couple createCompleteCouple() {
        return Couple.from(
                1L,
                LocalDate.of(2024, 1, 1),
                MemberId.of(100L),
                MemberId.of(200L),
                CoupleState.ALIVE,
                createSnapshot("first", LoveTypeCategory.STABLE_TYPE, 20f, 10f),
                createSnapshot("second", LoveTypeCategory.ANXIETY_TYPE, 40f, 30f),
                LocalDateTime.now(),
                LocalDateTime.now(),
                null
        );
    }

    private CoupleMemberSnapshot createSnapshot(String nickname, LoveTypeCategory category, float avoidanceRate, float anxietyRate) {
        return CoupleMemberSnapshot.from(
                nickname,
                category,
                avoidanceRate,
                anxietyRate
        );
    }

    private CoupleMemberSnapshot createSnapshot(String nickname) {
        return CoupleMemberSnapshot.from(
                nickname,
                LoveTypeCategory.STABLE_TYPE,
                20f,
                10f
        );
    }
}