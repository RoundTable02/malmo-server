package makeus.cmc.malmo.domain_service;

import makeus.cmc.malmo.domain.model.couple.Couple;
import makeus.cmc.malmo.domain.model.value.MemberId;
import makeus.cmc.malmo.domain.service.CoupleDomainService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
@DisplayName("CoupleDomainService 단위 테스트")
class CoupleDomainServiceTest {

    @InjectMocks
    private CoupleDomainService coupleDomainService;

    @Nested
    @DisplayName("커플 생성 기능")
    class CreateCoupleFeature {

        @Test
        @DisplayName("성공: 멤버 ID들과 시작 날짜로 커플을 생성한다")
        void givenMemberIdsAndStartDate_whenCreateCouple_thenReturnCouple() {
            // Given
            MemberId memberId = MemberId.of(1L);
            MemberId partnerId = MemberId.of(2L);
            LocalDate startLoveDate = LocalDate.of(2024, 1, 1);

            // When
            Couple result = coupleDomainService.createCoupleByInviteCode(memberId, partnerId, startLoveDate);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.getStartLoveDate()).isEqualTo(startLoveDate);
            assertThat(result.getCoupleMembers()).hasSize(2);
            assertThat(result.getCoupleMembers().get(0).getMemberId()).isEqualTo(memberId);
            assertThat(result.getCoupleMembers().get(1).getMemberId()).isEqualTo(partnerId);
        }

        @Test
        @DisplayName("성공: 다른 멤버 ID들로 커플을 생성한다")
        void givenDifferentMemberIds_whenCreateCouple_thenReturnCouple() {
            // Given
            MemberId memberId = MemberId.of(3L);
            MemberId partnerId = MemberId.of(5L);
            LocalDate startLoveDate = LocalDate.of(2023, 6, 15);

            // When
            Couple result = coupleDomainService.createCoupleByInviteCode(memberId, partnerId, startLoveDate);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.getStartLoveDate()).isEqualTo(startLoveDate);
            assertThat(result.getCoupleMembers()).hasSize(2);
            assertThat(result.getCoupleMembers().get(0).getMemberId()).isEqualTo(memberId);
            assertThat(result.getCoupleMembers().get(1).getMemberId()).isEqualTo(partnerId);
        }

        @Test
        @DisplayName("경계값: 오늘 날짜로 커플을 생성한다")
        void givenTodayDate_whenCreateCouple_thenReturnCouple() {
            // Given
            MemberId memberId = MemberId.of(1L);
            MemberId partnerId = MemberId.of(2L);
            LocalDate todayDate = LocalDate.now();

            // When
            Couple result = coupleDomainService.createCoupleByInviteCode(memberId, partnerId, todayDate);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.getStartLoveDate()).isEqualTo(todayDate);
            assertThat(result.getCoupleMembers()).hasSize(2);
        }

        @Test
        @DisplayName("경계값: 과거 날짜로 커플을 생성한다")
        void givenPastDate_whenCreateCouple_thenReturnCouple() {
            // Given
            MemberId memberId = MemberId.of(1L);
            MemberId partnerId = MemberId.of(2L);
            LocalDate pastDate = LocalDate.of(2020, 1, 1);

            // When
            Couple result = coupleDomainService.createCoupleByInviteCode(memberId, partnerId, pastDate);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.getStartLoveDate()).isEqualTo(pastDate);
            assertThat(result.getCoupleMembers()).hasSize(2);
        }

        @Test
        @DisplayName("경계값: 최대 멤버 ID로 커플을 생성한다")
        void givenMaxMemberId_whenCreateCouple_thenReturnCouple() {
            // Given
            MemberId memberId = MemberId.of(Long.MAX_VALUE - 1);
            MemberId partnerId = MemberId.of(Long.MAX_VALUE);
            LocalDate startLoveDate = LocalDate.of(2024, 1, 1);

            // When
            Couple result = coupleDomainService.createCoupleByInviteCode(memberId, partnerId, startLoveDate);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.getStartLoveDate()).isEqualTo(startLoveDate);
            assertThat(result.getCoupleMembers()).hasSize(2);
            assertThat(result.getCoupleMembers().get(0).getMemberId()).isEqualTo(memberId);
            assertThat(result.getCoupleMembers().get(1).getMemberId()).isEqualTo(partnerId);
        }

        @Test
        @DisplayName("경계값: 최소 멤버 ID로 커플을 생성한다")
        void givenMinMemberId_whenCreateCouple_thenReturnCouple() {
            // Given
            MemberId memberId = MemberId.of(1L);
            MemberId partnerId = MemberId.of(2L);
            LocalDate startLoveDate = LocalDate.of(2024, 1, 1);

            // When
            Couple result = coupleDomainService.createCoupleByInviteCode(memberId, partnerId, startLoveDate);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.getStartLoveDate()).isEqualTo(startLoveDate);
            assertThat(result.getCoupleMembers()).hasSize(2);
            assertThat(result.getCoupleMembers().get(0).getMemberId()).isEqualTo(memberId);
            assertThat(result.getCoupleMembers().get(1).getMemberId()).isEqualTo(partnerId);
        }
    }
}