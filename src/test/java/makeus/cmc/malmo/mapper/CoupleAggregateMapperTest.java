package makeus.cmc.malmo.mapper;

import makeus.cmc.malmo.adaptor.out.persistence.entity.couple.CoupleEntity;
import makeus.cmc.malmo.adaptor.out.persistence.entity.couple.CoupleMemberEntity;
import makeus.cmc.malmo.adaptor.out.persistence.entity.value.CoupleEntityId;
import makeus.cmc.malmo.adaptor.out.persistence.entity.value.MemberEntityId;
import makeus.cmc.malmo.adaptor.out.persistence.mapper.CoupleAggregateMapper;
import makeus.cmc.malmo.domain.model.couple.Couple;
import makeus.cmc.malmo.domain.model.couple.CoupleMember;
import makeus.cmc.malmo.domain.value.state.CoupleMemberState;
import makeus.cmc.malmo.domain.value.state.CoupleState;
import makeus.cmc.malmo.domain.value.id.CoupleId;
import makeus.cmc.malmo.domain.value.id.MemberId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

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
            assertThat(result.getCoupleMembers()).hasSize(2);
            assertThat(result.getCreatedAt()).isNotNull();
            assertThat(result.getModifiedAt()).isNotNull();
            assertThat(result.getDeletedAt()).isNull();

            // CoupleMember 검증
            CoupleMember firstMember = result.getCoupleMembers().get(0);
            assertThat(firstMember.getId()).isEqualTo(10L);
            assertThat(firstMember.getMemberId().getValue()).isEqualTo(100L);
            assertThat(firstMember.getCoupleId().getValue()).isEqualTo(1L);
            assertThat(firstMember.getCoupleMemberState()).isEqualTo(CoupleMemberState.ALIVE);

            CoupleMember secondMember = result.getCoupleMembers().get(1);
            assertThat(secondMember.getId()).isEqualTo(20L);
            assertThat(secondMember.getMemberId().getValue()).isEqualTo(200L);
            assertThat(secondMember.getCoupleId().getValue()).isEqualTo(1L);
            assertThat(secondMember.getCoupleMemberState()).isEqualTo(CoupleMemberState.ALIVE);
        }

        @Test
        @DisplayName("빈 CoupleMember 리스트를 가진 Entity를 변환한다")
        void givenEntityWithEmptyMembers_whenToDomain_thenReturnsCoupleWithEmptyMembers() {
            // given
            CoupleEntity entity = createEntityWithEmptyMembers();

            // when
            Couple result = coupleAggregateMapper.toDomain(entity);

            // then
            assertThat(result).isNotNull();
            assertThat(result.getCoupleMembers()).isEmpty();
        }

        @Test
        @DisplayName("다양한 CoupleState를 가진 Entity를 변환한다")
        void givenDifferentCoupleStates_whenToDomain_thenReturnsCorrectStates() {
            // given & when & then
            CoupleEntity inactiveEntity = createEntityWithState(CoupleState.DELETED);
            Couple inactiveResult = coupleAggregateMapper.toDomain(inactiveEntity);
            assertThat(inactiveResult.getCoupleState()).isEqualTo(CoupleState.DELETED);

            CoupleEntity deletedEntity = createEntityWithState(CoupleState.DELETED);
            Couple deletedResult = coupleAggregateMapper.toDomain(deletedEntity);
            assertThat(deletedResult.getCoupleState()).isEqualTo(CoupleState.DELETED);
        }

        @Test
        @DisplayName("다양한 CoupleMemberState를 가진 Entity를 변환한다")
        void givenDifferentCoupleMemberStates_whenToDomain_thenReturnsCorrectMemberStates() {
            // given
            CoupleEntity entity = createEntityWithDifferentMemberStates();

            // when
            Couple result = coupleAggregateMapper.toDomain(entity);

            // then
            assertThat(result.getCoupleMembers()).hasSize(3);
            assertThat(result.getCoupleMembers().get(0).getCoupleMemberState()).isEqualTo(CoupleMemberState.ALIVE);
            assertThat(result.getCoupleMembers().get(1).getCoupleMemberState()).isEqualTo(CoupleMemberState.NOT_ALLOCATED);
            assertThat(result.getCoupleMembers().get(2).getCoupleMemberState()).isEqualTo(CoupleMemberState.DELETED);
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
            assertThat(result.getCoupleMembers()).hasSize(2);
            assertThat(result.getCreatedAt()).isNotNull();
            assertThat(result.getModifiedAt()).isNotNull();
            assertThat(result.getDeletedAt()).isNull();

            // CoupleMemberEntity 검증
            CoupleMemberEntity firstMemberEntity = result.getCoupleMembers().get(0);
            assertThat(firstMemberEntity.getId()).isEqualTo(10L);
            assertThat(firstMemberEntity.getMemberEntityId().getValue()).isEqualTo(100L);
            assertThat(firstMemberEntity.getCoupleEntityId().getValue()).isEqualTo(1L);
            assertThat(firstMemberEntity.getCoupleMemberState()).isEqualTo(CoupleMemberState.ALIVE);

            CoupleMemberEntity secondMemberEntity = result.getCoupleMembers().get(1);
            assertThat(secondMemberEntity.getId()).isEqualTo(20L);
            assertThat(secondMemberEntity.getMemberEntityId().getValue()).isEqualTo(200L);
            assertThat(secondMemberEntity.getCoupleEntityId().getValue()).isEqualTo(1L);
            assertThat(secondMemberEntity.getCoupleMemberState()).isEqualTo(CoupleMemberState.ALIVE);
        }

        @Test
        @DisplayName("빈 CoupleMember 리스트를 가진 Couple을 변환한다")
        void givenCoupleWithEmptyMembers_whenToEntity_thenReturnsEntityWithEmptyMembers() {
            // given
            Couple domain = createCoupleWithEmptyMembers();

            // when
            CoupleEntity result = coupleAggregateMapper.toEntity(domain);

            // then
            assertThat(result).isNotNull();
            assertThat(result.getCoupleMembers()).isEmpty();
        }

        @Test
        @DisplayName("다양한 CoupleState를 가진 Couple을 변환한다")
        void givenDifferentCoupleStates_whenToEntity_thenReturnsCorrectStates() {
            // given & when & then
            Couple inactiveCouple = createCoupleWithState(CoupleState.DELETED);
            CoupleEntity inactiveResult = coupleAggregateMapper.toEntity(inactiveCouple);
            assertThat(inactiveResult.getCoupleState()).isEqualTo(CoupleState.DELETED);

            Couple deletedCouple = createCoupleWithState(CoupleState.DELETED);
            CoupleEntity deletedResult = coupleAggregateMapper.toEntity(deletedCouple);
            assertThat(deletedResult.getCoupleState()).isEqualTo(CoupleState.DELETED);
        }

        @Test
        @DisplayName("다양한 CoupleMemberState를 가진 Couple을 변환한다")
        void givenDifferentCoupleMemberStates_whenToEntity_thenReturnsCorrectMemberStates() {
            // given
            Couple domain = createCoupleWithDifferentMemberStates();

            // when
            CoupleEntity result = coupleAggregateMapper.toEntity(domain);

            // then
            assertThat(result.getCoupleMembers()).hasSize(3);
            assertThat(result.getCoupleMembers().get(0).getCoupleMemberState()).isEqualTo(CoupleMemberState.ALIVE);
            assertThat(result.getCoupleMembers().get(1).getCoupleMemberState()).isEqualTo(CoupleMemberState.NOT_ALLOCATED);
            assertThat(result.getCoupleMembers().get(2).getCoupleMemberState()).isEqualTo(CoupleMemberState.DELETED);
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

        @Test
        @DisplayName("null CoupleMemberState를 가진 Couple을 변환한다")
        void givenCoupleWithNullMemberState_whenToEntity_thenReturnsEntityWithNullMemberState() {
            // given
            Couple domain = createCoupleWithNullMemberState();

            // when
            CoupleEntity result = coupleAggregateMapper.toEntity(domain);

            // then
            assertThat(result).isNotNull();
            assertThat(result.getCoupleMembers()).hasSize(1);
            assertThat(result.getCoupleMembers().get(0).getCoupleMemberState()).isNull();
        }
    }

    // Test data creation methods for Entity
    private CoupleEntity createCompleteEntity() {
        List<CoupleMemberEntity> members = Arrays.asList(
                createCoupleMemberEntity(10L, 100L, 1L, CoupleMemberState.ALIVE),
                createCoupleMemberEntity(20L, 200L, 1L, CoupleMemberState.ALIVE)
        );

        return CoupleEntity.builder()
                .id(1L)
                .startLoveDate(LocalDate.of(2024, 1, 1))
                .coupleState(CoupleState.ALIVE)
                .coupleMembers(members)
                .createdAt(LocalDateTime.now())
                .modifiedAt(LocalDateTime.now())
                .deletedAt(null)
                .build();
    }

    private CoupleEntity createEntityWithEmptyMembers() {
        return CoupleEntity.builder()
                .id(1L)
                .startLoveDate(LocalDate.of(2024, 1, 1))
                .coupleState(CoupleState.ALIVE)
                .coupleMembers(Collections.emptyList())
                .build();
    }

    private CoupleEntity createEntityWithState(CoupleState state) {
        return CoupleEntity.builder()
                .id(1L)
                .startLoveDate(LocalDate.of(2024, 1, 1))
                .coupleState(state)
                .coupleMembers(Collections.emptyList())
                .build();
    }

    private CoupleEntity createEntityWithDifferentMemberStates() {
        List<CoupleMemberEntity> members = Arrays.asList(
                createCoupleMemberEntity(10L, 100L, 1L, CoupleMemberState.ALIVE),
                createCoupleMemberEntity(20L, 200L, 1L, CoupleMemberState.NOT_ALLOCATED),
                createCoupleMemberEntity(30L, 300L, 1L, CoupleMemberState.DELETED)
        );

        return CoupleEntity.builder()
                .id(1L)
                .startLoveDate(LocalDate.of(2024, 1, 1))
                .coupleState(CoupleState.ALIVE)
                .coupleMembers(members)
                .build();
    }

    private CoupleEntity createEntityWithNullState() {
        return CoupleEntity.builder()
                .id(1L)
                .startLoveDate(LocalDate.of(2024, 1, 1))
                .coupleState(null)
                .coupleMembers(Collections.emptyList())
                .build();
    }

    private CoupleMemberEntity createCoupleMemberEntity(Long id, Long memberId, Long coupleId, CoupleMemberState state) {
        return CoupleMemberEntity.builder()
                .id(id)
                .memberEntityId(MemberEntityId.of(memberId))
                .coupleEntityId(CoupleEntityId.of(coupleId))
                .coupleMemberState(state)
                .createdAt(LocalDateTime.now())
                .modifiedAt(LocalDateTime.now())
                .build();
    }

    // Test data creation methods for Domain
    private Couple createCompleteCouple() {
        List<CoupleMember> members = Arrays.asList(
                createCoupleMemberFromStatic(10L, 100L, 1L, CoupleMemberState.ALIVE),
                createCoupleMemberFromStatic(20L, 200L, 1L, CoupleMemberState.ALIVE)
        );

        return Couple.from(
                1L,
                LocalDate.of(2024, 1, 1),
                CoupleState.ALIVE,
                members,
                LocalDateTime.now(),
                LocalDateTime.now(),
                null
        );
    }

    private Couple createCoupleWithEmptyMembers() {
        return Couple.from(
                1L,
                LocalDate.of(2024, 1, 1),
                CoupleState.ALIVE,
                Collections.emptyList(),
                LocalDateTime.now(),
                LocalDateTime.now(),
                null
        );
    }

    private Couple createCoupleWithState(CoupleState state) {
        return Couple.from(
                1L,
                LocalDate.of(2024, 1, 1),
                state,
                Collections.emptyList(),
                LocalDateTime.now(),
                LocalDateTime.now(),
                null
        );
    }

    private Couple createCoupleWithDifferentMemberStates() {
        List<CoupleMember> members = Arrays.asList(
                createCoupleMemberFromStatic(10L, 100L, 1L, CoupleMemberState.ALIVE),
                createCoupleMemberFromStatic(20L, 200L, 1L, CoupleMemberState.NOT_ALLOCATED),
                createCoupleMemberFromStatic(30L, 300L, 1L, CoupleMemberState.DELETED)
        );

        return Couple.from(
                1L,
                LocalDate.of(2024, 1, 1),
                CoupleState.ALIVE,
                members,
                LocalDateTime.now(),
                LocalDateTime.now(),
                null
        );
    }

    private Couple createCoupleWithNullState() {
        return Couple.from(
                1L,
                LocalDate.of(2024, 1, 1),
                null,
                Collections.emptyList(),
                LocalDateTime.now(),
                LocalDateTime.now(),
                null
        );
    }

    private Couple createCoupleWithNullMemberState() {
        List<CoupleMember> members = Arrays.asList(
                createCoupleMemberFromStatic(10L, 100L, 1L, null)
        );

        return Couple.from(
                1L,
                LocalDate.of(2024, 1, 1),
                CoupleState.ALIVE,
                members,
                LocalDateTime.now(),
                LocalDateTime.now(),
                null
        );
    }

    private CoupleMember createCoupleMemberFromStatic(Long id, Long memberId, Long coupleId, CoupleMemberState state) {
        return CoupleMember.from(
                id,
                MemberId.of(memberId),
                CoupleId.of(coupleId),
                state,
                LocalDateTime.now(),
                LocalDateTime.now(),
                null
        );
    }
}