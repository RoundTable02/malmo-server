package makeus.cmc.malmo.mapper;

import makeus.cmc.malmo.adaptor.out.persistence.entity.value.MemberEntityId;
import makeus.cmc.malmo.domain.model.member.CoupleCode;
import makeus.cmc.malmo.domain.model.value.MemberId;
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
@DisplayName("CoupleCodeMapper 테스트")
class CoupleCodeMapperTest {

    @InjectMocks
    private CoupleCodeMapper coupleCodeMapper;

    @Nested
    @DisplayName("Entity를 Domain으로 변환할 때")
    class ToDomainTest {

        @Test
        @DisplayName("모든 필드가 있는 CoupleCodeEntity를 CoupleCode로 변환한다")
        void givenCompleteEntity_whenToDomain_thenReturnsCompleteCoupleCode() {
            // given
            CoupleCodeEntity entity = createCompleteEntity();

            // when
            CoupleCode result = coupleCodeMapper.toDomain(entity);

            // then
            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(1L);
            assertThat(result.getInviteCode()).isEqualTo("INVITE123");
            assertThat(result.getStartLoveDate()).isEqualTo(LocalDate.of(2024, 1, 1));
            assertThat(result.getMemberId().getValue()).isEqualTo(100L);
            assertThat(result.getCreatedAt()).isNotNull();
            assertThat(result.getModifiedAt()).isNotNull();
            assertThat(result.getDeletedAt()).isNull();
        }

        @Test
        @DisplayName("일부 필드가 null인 Entity를 변환한다")
        void givenEntityWithNullFields_whenToDomain_thenReturnsCoupleCodeWithNullFields() {
            // given
            CoupleCodeEntity entity = createEntityWithNullFields();

            // when
            CoupleCode result = coupleCodeMapper.toDomain(entity);

            // then
            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(1L);
            assertThat(result.getInviteCode()).isNull();
            assertThat(result.getStartLoveDate()).isNull();
            assertThat(result.getMemberId().getValue()).isNull();
        }

        @Test
        @DisplayName("삭제된 CoupleCodeEntity를 변환한다")
        void givenDeletedEntity_whenToDomain_thenReturnsDeletedCoupleCode() {
            // given
            LocalDateTime deletedAt = LocalDateTime.now();
            CoupleCodeEntity entity = createDeletedEntity(deletedAt);

            // when
            CoupleCode result = coupleCodeMapper.toDomain(entity);

            // then
            assertThat(result).isNotNull();
            assertThat(result.getDeletedAt()).isEqualTo(deletedAt);
        }

        @Test
        @DisplayName("null ID를 가진 Entity를 변환한다")
        void givenEntityWithNullId_whenToDomain_thenReturnsCoupleCodeWithNullId() {
            // given
            CoupleCodeEntity entity = createEntityWithNullId();

            // when
            CoupleCode result = coupleCodeMapper.toDomain(entity);

            // then
            assertThat(result).isNotNull();
            assertThat(result.getId()).isNull();
            assertThat(result.getInviteCode()).isEqualTo("INVITE456");
        }
    }

    @Nested
    @DisplayName("Domain을 Entity로 변환할 때")
    class ToEntityTest {

        @Test
        @DisplayName("모든 필드가 있는 CoupleCode를 CoupleCodeEntity로 변환한다")
        void givenCompleteCoupleCode_whenToEntity_thenReturnsCompleteEntity() {
            // given
            CoupleCode domain = createCompleteCoupleCode();

            // when
            CoupleCodeEntity result = coupleCodeMapper.toEntity(domain);

            // then
            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(1L);
            assertThat(result.getInviteCode()).isEqualTo("INVITE123");
            assertThat(result.getStartLoveDate()).isEqualTo(LocalDate.of(2024, 1, 1));
            assertThat(result.getMemberEntityId().getValue()).isEqualTo(100L);
            assertThat(result.getCreatedAt()).isNotNull();
            assertThat(result.getModifiedAt()).isNotNull();
            assertThat(result.getDeletedAt()).isNull();
        }

        @Test
        @DisplayName("일부 필드가 null인 CoupleCode를 변환한다")
        void givenCoupleCodeWithNullFields_whenToEntity_thenReturnsEntityWithNullFields() {
            // given
            CoupleCode domain = createCoupleCodeWithNullFields();

            // when
            CoupleCodeEntity result = coupleCodeMapper.toEntity(domain);

            // then
            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(1L);
            assertThat(result.getInviteCode()).isNull();
            assertThat(result.getStartLoveDate()).isNull();
            assertThat(result.getMemberEntityId().getValue()).isNull();
        }

        @Test
        @DisplayName("삭제된 CoupleCode를 변환한다")
        void givenDeletedCoupleCode_whenToEntity_thenReturnsDeletedEntity() {
            // given
            LocalDateTime deletedAt = LocalDateTime.now();
            CoupleCode domain = createDeletedCoupleCode(deletedAt);

            // when
            CoupleCodeEntity result = coupleCodeMapper.toEntity(domain);

            // then
            assertThat(result).isNotNull();
            assertThat(result.getDeletedAt()).isEqualTo(deletedAt);
        }

        @Test
        @DisplayName("null ID를 가진 CoupleCode를 변환한다")
        void givenCoupleCodeWithNullId_whenToEntity_thenReturnsEntityWithNullId() {
            // given
            CoupleCode domain = createCoupleCodeWithNullId();

            // when
            CoupleCodeEntity result = coupleCodeMapper.toEntity(domain);

            // then
            assertThat(result).isNotNull();
            assertThat(result.getId()).isNull();
            assertThat(result.getInviteCode()).isEqualTo("INVITE456");
        }

        @Test
        @DisplayName("과거 날짜의 startLoveDate를 가진 CoupleCode를 변환한다")
        void givenCoupleCodeWithPastDate_whenToEntity_thenReturnsEntityWithPastDate() {
            // given
            LocalDate pastDate = LocalDate.of(2020, 12, 25);
            CoupleCode domain = createCoupleCodeWithCustomDate(pastDate);

            // when
            CoupleCodeEntity result = coupleCodeMapper.toEntity(domain);

            // then
            assertThat(result).isNotNull();
            assertThat(result.getStartLoveDate()).isEqualTo(pastDate);
        }

        @Test
        @DisplayName("미래 날짜의 startLoveDate를 가진 CoupleCode를 변환한다")
        void givenCoupleCodeWithFutureDate_whenToEntity_thenReturnsEntityWithFutureDate() {
            // given
            LocalDate futureDate = LocalDate.of(2025, 12, 25);
            CoupleCode domain = createCoupleCodeWithCustomDate(futureDate);

            // when
            CoupleCodeEntity result = coupleCodeMapper.toEntity(domain);

            // then
            assertThat(result).isNotNull();
            assertThat(result.getStartLoveDate()).isEqualTo(futureDate);
        }
    }

    // Test data creation methods
    private CoupleCodeEntity createCompleteEntity() {
        return CoupleCodeEntity.builder()
                .id(1L)
                .inviteCode("INVITE123")
                .startLoveDate(LocalDate.of(2024, 1, 1))
                .memberEntityId(MemberEntityId.of(100L))
                .createdAt(LocalDateTime.now())
                .modifiedAt(LocalDateTime.now())
                .deletedAt(null)
                .build();
    }

    private CoupleCodeEntity createEntityWithNullFields() {
        return CoupleCodeEntity.builder()
                .id(1L)
                .inviteCode(null)
                .startLoveDate(null)
                .memberEntityId(MemberEntityId.of(null))
                .build();
    }

    private CoupleCodeEntity createDeletedEntity(LocalDateTime deletedAt) {
        return CoupleCodeEntity.builder()
                .id(1L)
                .inviteCode("DELETED123")
                .startLoveDate(LocalDate.of(2024, 1, 1))
                .memberEntityId(MemberEntityId.of(100L))
                .deletedAt(deletedAt)
                .build();
    }

    private CoupleCodeEntity createEntityWithNullId() {
        return CoupleCodeEntity.builder()
                .id(null)
                .inviteCode("INVITE456")
                .startLoveDate(LocalDate.of(2024, 2, 1))
                .memberEntityId(MemberEntityId.of(100L))
                .build();
    }

    private CoupleCode createCompleteCoupleCode() {
        return CoupleCode.builder()
                .id(1L)
                .inviteCode("INVITE123")
                .startLoveDate(LocalDate.of(2024, 1, 1))
                .memberId(MemberId.of(100L))
                .createdAt(LocalDateTime.now())
                .modifiedAt(LocalDateTime.now())
                .deletedAt(null)
                .build();
    }

    private CoupleCode createCoupleCodeWithNullFields() {
        return CoupleCode.builder()
                .id(1L)
                .inviteCode(null)
                .startLoveDate(null)
                .memberId(MemberId.of(null))
                .build();
    }

    private CoupleCode createDeletedCoupleCode(LocalDateTime deletedAt) {
        return CoupleCode.builder()
                .id(1L)
                .inviteCode("DELETED123")
                .startLoveDate(LocalDate.of(2024, 1, 1))
                .memberId(MemberId.of(100L))
                .deletedAt(deletedAt)
                .build();
    }

    private CoupleCode createCoupleCodeWithNullId() {
        return CoupleCode.builder()
                .id(null)
                .inviteCode("INVITE456")
                .startLoveDate(LocalDate.of(2024, 2, 1))
                .memberId(MemberId.of(100L))
                .build();
    }

    private CoupleCode createCoupleCodeWithCustomDate(LocalDate date) {
        return CoupleCode.builder()
                .id(1L)
                .inviteCode("CUSTOM123")
                .startLoveDate(date)
                .memberId(MemberId.of(100L))
                .build();
    }
}