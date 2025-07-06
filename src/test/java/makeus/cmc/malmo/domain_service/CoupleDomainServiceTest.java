package makeus.cmc.malmo.domain_service;

import makeus.cmc.malmo.domain.model.couple.Couple;
import makeus.cmc.malmo.domain.model.member.CoupleCode;
import makeus.cmc.malmo.domain.model.member.Member;
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
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CoupleDomainService 단위 테스트")
class CoupleDomainServiceTest {

    @InjectMocks
    private CoupleDomainService coupleDomainService;

    @Nested
    @DisplayName("커플 생성 기능")
    class CreateCoupleFeature {

        @Test
        @DisplayName("성공: 멤버와 커플 코드로 커플을 생성한다")
        void givenMemberAndCoupleCode_whenCreateCouple_thenReturnCouple() {
            // Given
            Member member = mock(Member.class);
            CoupleCode coupleCode = mock(CoupleCode.class);
            MemberId coupleCodeMemberId = MemberId.of(2L);
            LocalDate startLoveDate = LocalDate.of(2024, 1, 1);

            given(member.getId()).willReturn(1L);
            given(coupleCode.getMemberId()).willReturn(coupleCodeMemberId);
            given(coupleCode.getStartLoveDate()).willReturn(startLoveDate);

            // When
            Couple result = coupleDomainService.createCoupleByCoupleCode(member, coupleCode);

            // Then
            assertThat(result).isNotNull();
            then(member).should().getId();
            then(coupleCode).should().getMemberId();
            then(coupleCode).should().getStartLoveDate();
        }

        @Test
        @DisplayName("성공: 다른 멤버 ID들로 커플을 생성한다")
        void givenDifferentMemberIds_whenCreateCouple_thenReturnCouple() {
            // Given
            Member member = mock(Member.class);
            CoupleCode coupleCode = mock(CoupleCode.class);
            MemberId coupleCodeMemberId = MemberId.of(5L);
            LocalDate startLoveDate = LocalDate.of(2023, 6, 15);

            given(member.getId()).willReturn(3L);
            given(coupleCode.getMemberId()).willReturn(coupleCodeMemberId);
            given(coupleCode.getStartLoveDate()).willReturn(startLoveDate);

            // When
            Couple result = coupleDomainService.createCoupleByCoupleCode(member, coupleCode);

            // Then
            assertThat(result).isNotNull();
            then(member).should().getId();
            then(coupleCode).should().getMemberId();
            then(coupleCode).should().getStartLoveDate();
        }

        @Test
        @DisplayName("경계값: 오늘 날짜로 커플을 생성한다")
        void givenTodayDate_whenCreateCouple_thenReturnCouple() {
            // Given
            Member member = mock(Member.class);
            CoupleCode coupleCode = mock(CoupleCode.class);
            MemberId coupleCodeMemberId = MemberId.of(2L);
            LocalDate todayDate = LocalDate.now();

            given(member.getId()).willReturn(1L);
            given(coupleCode.getMemberId()).willReturn(coupleCodeMemberId);
            given(coupleCode.getStartLoveDate()).willReturn(todayDate);

            // When
            Couple result = coupleDomainService.createCoupleByCoupleCode(member, coupleCode);

            // Then
            assertThat(result).isNotNull();
            then(member).should().getId();
            then(coupleCode).should().getMemberId();
            then(coupleCode).should().getStartLoveDate();
        }

        @Test
        @DisplayName("경계값: 과거 날짜로 커플을 생성한다")
        void givenPastDate_whenCreateCouple_thenReturnCouple() {
            // Given
            Member member = mock(Member.class);
            CoupleCode coupleCode = mock(CoupleCode.class);
            MemberId coupleCodeMemberId = MemberId.of(2L);
            LocalDate pastDate = LocalDate.of(2020, 1, 1);

            given(member.getId()).willReturn(1L);
            given(coupleCode.getMemberId()).willReturn(coupleCodeMemberId);
            given(coupleCode.getStartLoveDate()).willReturn(pastDate);

            // When
            Couple result = coupleDomainService.createCoupleByCoupleCode(member, coupleCode);

            // Then
            assertThat(result).isNotNull();
            then(member).should().getId();
            then(coupleCode).should().getMemberId();
            then(coupleCode).should().getStartLoveDate();
        }

        @Test
        @DisplayName("경계값: 최대 멤버 ID로 커플을 생성한다")
        void givenMaxMemberId_whenCreateCouple_thenReturnCouple() {
            // Given
            Member member = mock(Member.class);
            CoupleCode coupleCode = mock(CoupleCode.class);
            MemberId coupleCodeMemberId = MemberId.of(Long.MAX_VALUE);
            LocalDate startLoveDate = LocalDate.of(2024, 1, 1);

            given(member.getId()).willReturn(Long.MAX_VALUE - 1);
            given(coupleCode.getMemberId()).willReturn(coupleCodeMemberId);
            given(coupleCode.getStartLoveDate()).willReturn(startLoveDate);

            // When
            Couple result = coupleDomainService.createCoupleByCoupleCode(member, coupleCode);

            // Then
            assertThat(result).isNotNull();
            then(member).should().getId();
            then(coupleCode).should().getMemberId();
            then(coupleCode).should().getStartLoveDate();
        }

        @Test
        @DisplayName("경계값: 최소 멤버 ID로 커플을 생성한다")
        void givenMinMemberId_whenCreateCouple_thenReturnCouple() {
            // Given
            Member member = mock(Member.class);
            CoupleCode coupleCode = mock(CoupleCode.class);
            MemberId coupleCodeMemberId = MemberId.of(2L);
            LocalDate startLoveDate = LocalDate.of(2024, 1, 1);

            given(member.getId()).willReturn(1L);
            given(coupleCode.getMemberId()).willReturn(coupleCodeMemberId);
            given(coupleCode.getStartLoveDate()).willReturn(startLoveDate);

            // When
            Couple result = coupleDomainService.createCoupleByCoupleCode(member, coupleCode);

            // Then
            assertThat(result).isNotNull();
            then(member).should().getId();
            then(coupleCode).should().getMemberId();
            then(coupleCode).should().getStartLoveDate();
        }
    }
}