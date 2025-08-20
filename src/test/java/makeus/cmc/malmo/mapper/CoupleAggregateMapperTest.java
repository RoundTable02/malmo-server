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
            assertThat(result.getId()).isEqualTo(1L);
            assertThat(result.getStartLoveDate()).isEqualTo(LocalDate.of(2024, 1, 1));
            assertThat(result.getCoupleState()).isEqualTo(CoupleState.ALIVE);
            assertThat(result.getFirstMemberId().getValue()).isEqualTo(100L);
            assertThat(result.getSecondMemberId().getValue()).isEqualTo(200L);
            assertThat(result.getCreatedAt()).isNotNull();
            assertThat(result.getModifiedAt()).isNotNull();
            assertThat(result.getDeletedAt()).isNull();

            // CoupleMemberSnapshot 검증
            CoupleMemberSnapshot firstSnapshot = result.getFirstMemberSnapshot();
            assertThat(firstSnapshot.getNickname()).isEqualTo("first");
            assertThat(firstSnapshot.getLoveTypeCategory()).isEqualTo(LoveTypeCategory.STABLE_TYPE);
            assertThat(firstSnapshot.getAnxietyRate()).isEqualTo(10);
            assertThat(firstSnapshot.getAvoidanceRate()).isEqualTo(20);

            CoupleMemberSnapshot secondSnapshot = result.getSecondMemberSnapshot();
            assertThat(secondSnapshot.getNickname()).isEqualTo("second");
        }

        @Test
        @DisplayName("다양한 CoupleState를 가진 Entity를 변환한다")
        void givenDifferentCoupleStates_whenToDomain_thenReturnsCorrectStates() {
            // given & when & then
            CoupleEntity deletedEntity = createEntityWithState(CoupleState.DELETED);
            Couple deletedResult = coupleAggregateMapper.toDomain(deletedEntity);
            assertThat(deletedResult.getCoupleState()).isEqualTo(CoupleState.DELETED);
        }

        @Test
        @DisplayName("null CoupleState를 가진 Entity를 변환한다")
        void givenEntityWithNullCoupleState_whenToDomain_thenReturnsCoupleWithNullState() {
            // given
            CoupleEntity entity = createEntityWithNullState();

            // when
            Couple result = coupleAggregateMapper.toDomain(entity);

            // then
            assertThat(result).isNotNull();
            assertThat(result.getCoupleState()).isNull();
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
            assertThat(result.getId()).isEqualTo(1L);
            assertThat(result.getStartLoveDate()).isEqualTo(LocalDate.of(2024, 1, 1));
            assertThat(result.getCoupleState()).isEqualTo(CoupleState.ALIVE);
            assertThat(result.getFirstMemberId().getValue()).isEqualTo(100L);
            assertThat(result.getSecondMemberId().getValue()).isEqualTo(200L);
            assertThat(result.getCreatedAt()).isNotNull();
            assertThat(result.getModifiedAt()).isNotNull();
            assertThat(result.getDeletedAt()).isNull();

            // CoupleMemberSnapshotEntity 검증
            CoupleMemberSnapshotEntity firstSnapshotEntity = result.getFirstMemberSnapshot();
            assertThat(firstSnapshotEntity.getNickname()).isEqualTo("first");
            assertThat(firstSnapshotEntity.getLoveTypeCategory()).isEqualTo(LoveTypeCategory.STABLE_TYPE);
            assertThat(firstSnapshotEntity.getAnxietyRate()).isEqualTo(10);
            assertThat(firstSnapshotEntity.getAvoidanceRate()).isEqualTo(20);

            CoupleMemberSnapshotEntity secondSnapshotEntity = result.getSecondMemberSnapshot();
            assertThat(secondSnapshotEntity.getNickname()).isEqualTo("second");
        }

        @Test
        @DisplayName("다양한 CoupleState를 가진 Couple을 변환한다")
        void givenDifferentCoupleStates_whenToEntity_thenReturnsCorrectStates() {
            // given & when & then
            Couple deletedCouple = createCoupleWithState(CoupleState.DELETED);
            CoupleEntity deletedResult = coupleAggregateMapper.toEntity(deletedCouple);
            assertThat(deletedResult.getCoupleState()).isEqualTo(CoupleState.DELETED);
        }

        @Test
        @DisplayName("null CoupleState를 가진 Couple을 변환한다")
        void givenCoupleWithNullState_whenToEntity_thenReturnsEntityWithNullState() {
            // given
            Couple domain = createCoupleWithNullState();

            // when
            CoupleEntity result = coupleAggregateMapper.toEntity(domain);

            // then
            assertThat(result).isNotNull();
            assertThat(result.getCoupleState()).isNull();
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
                .firstMemberSnapshot(createSnapshotEntity("first"))
                .secondMemberSnapshot(createSnapshotEntity("second"))
                .createdAt(LocalDateTime.now())
                .modifiedAt(LocalDateTime.now())
                .deletedAt(null)
                .build();
    }

    private CoupleEntity createEntityWithState(CoupleState state) {
        return CoupleEntity.builder()
                .id(1L)
                .startLoveDate(LocalDate.of(2024, 1, 1))
                .coupleState(state)
                .build();
    }

    private CoupleEntity createEntityWithNullState() {
        return CoupleEntity.builder()
                .id(1L)
                .startLoveDate(LocalDate.of(2024, 1, 1))
                .coupleState(null)
                .build();
    }

    private CoupleMemberSnapshotEntity createSnapshotEntity(String nickname) {
        return CoupleMemberSnapshotEntity.builder()
                .nickname(nickname)
                .loveTypeCategory(LoveTypeCategory.STABLE_TYPE)
                .anxietyRate(10)
                .avoidanceRate(20)
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
                createSnapshot("first"),
                createSnapshot("second"),
                LocalDateTime.now(),
                LocalDateTime.now(),
                null
        );
    }

    private Couple createCoupleWithState(CoupleState state) {
        return Couple.from(
                1L,
                LocalDate.of(2024, 1, 1),
                MemberId.of(100L),
                MemberId.of(200L),
                state,
                createSnapshot("first"),
                createSnapshot("second"),
                LocalDateTime.now(),
                LocalDateTime.now(),
                null
        );
    }

    private Couple createCoupleWithNullState() {
        return Couple.from(
                1L,
                LocalDate.of(2024, 1, 1),
                MemberId.of(100L),
                MemberId.of(200L),
                null,
                createSnapshot("first"),
                createSnapshot("second"),
                LocalDateTime.now(),
                LocalDateTime.now(),
                null
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