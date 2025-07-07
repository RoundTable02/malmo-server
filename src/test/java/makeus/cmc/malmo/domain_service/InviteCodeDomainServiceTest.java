package makeus.cmc.malmo.domain_service;

import makeus.cmc.malmo.domain.exception.InviteCodeNotFoundException;
import makeus.cmc.malmo.domain.exception.InviteCodeGenerateFailedException;
import makeus.cmc.malmo.application.port.out.GenerateInviteCodePort;
import makeus.cmc.malmo.domain.model.member.CoupleCode;
import makeus.cmc.malmo.domain.model.member.Member;
import makeus.cmc.malmo.domain.model.value.MemberId;
import makeus.cmc.malmo.domain.service.InviteCodeDomainService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CoupleCodeDomainService 단위 테스트")
class InviteCodeDomainServiceTest {

    @Mock
    private LoadCoupleCodePort loadCoupleCodePort;

    @Mock
    private GenerateInviteCodePort generateInviteCodePort;

    @Mock
    private SaveCoupleCodePort saveCoupleCodePort;

    @InjectMocks
    private InviteCodeDomainService inviteCodeDomainService;

    @Nested
    @DisplayName("초대 코드로 커플 코드 조회 기능")
    class GetCoupleCodeByInviteCodeFeature {

        @Test
        @DisplayName("성공: 유효한 초대 코드로 커플 코드를 조회한다")
        void givenValidInviteCode_whenGetCoupleCodeByInviteCode_thenReturnCoupleCode() {
            // Given
            String inviteCode = "INVITE123";
            CoupleCode coupleCode = mock(CoupleCode.class);

            given(loadCoupleCodePort.loadCoupleCodeByInviteCode(inviteCode))
                    .willReturn(Optional.of(coupleCode));

            // When
            CoupleCode result = inviteCodeDomainService.getCoupleCodeByInviteCode(inviteCode);

            // Then
            assertThat(result).isEqualTo(coupleCode);
            then(loadCoupleCodePort).should().loadCoupleCodeByInviteCode(inviteCode);
        }

        @Test
        @DisplayName("실패: 존재하지 않는 초대 코드로 조회 시 CoupleCodeNotFoundException이 발생한다")
        void givenNonExistentInviteCode_whenGetCoupleCodeByInviteCode_thenThrowCoupleCodeNotFoundException() {
            // Given
            String inviteCode = "INVALID123";

            given(loadCoupleCodePort.loadCoupleCodeByInviteCode(inviteCode))
                    .willReturn(Optional.empty());

            // When & Then
            assertThatThrownBy(() -> inviteCodeDomainService.getCoupleCodeByInviteCode(inviteCode))
                    .isInstanceOf(InviteCodeNotFoundException.class);

            then(loadCoupleCodePort).should().loadCoupleCodeByInviteCode(inviteCode);
        }

        @Test
        @DisplayName("경계값: null 초대 코드로 조회 시 CoupleCodeNotFoundException이 발생한다")
        void givenNullInviteCode_whenGetCoupleCodeByInviteCode_thenThrowCoupleCodeNotFoundException() {
            // Given
            String inviteCode = null;

            given(loadCoupleCodePort.loadCoupleCodeByInviteCode(inviteCode))
                    .willReturn(Optional.empty());

            // When & Then
            assertThatThrownBy(() -> inviteCodeDomainService.getCoupleCodeByInviteCode(inviteCode))
                    .isInstanceOf(InviteCodeNotFoundException.class);

            then(loadCoupleCodePort).should().loadCoupleCodeByInviteCode(inviteCode);
        }

        @Test
        @DisplayName("경계값: 빈 문자열 초대 코드로 조회 시 CoupleCodeNotFoundException이 발생한다")
        void givenEmptyInviteCode_whenGetCoupleCodeByInviteCode_thenThrowCoupleCodeNotFoundException() {
            // Given
            String inviteCode = "";

            given(loadCoupleCodePort.loadCoupleCodeByInviteCode(inviteCode))
                    .willReturn(Optional.empty());

            // When & Then
            assertThatThrownBy(() -> inviteCodeDomainService.getCoupleCodeByInviteCode(inviteCode))
                    .isInstanceOf(InviteCodeNotFoundException.class);

            then(loadCoupleCodePort).should().loadCoupleCodeByInviteCode(inviteCode);
        }
    }

    @Nested
    @DisplayName("멤버 ID로 커플 코드 조회 기능")
    class GetCoupleCodeByMemberIdFeature {

        @Test
        @DisplayName("성공: 유효한 멤버 ID로 커플 코드를 조회한다")
        void givenValidMemberId_whenGetCoupleCodeByMemberId_thenReturnCoupleCode() {
            // Given
            MemberId memberId = MemberId.of(1L);
            CoupleCode coupleCode = mock(CoupleCode.class);

            given(loadCoupleCodePort.loadCoupleCodeByMemberId(memberId))
                    .willReturn(Optional.of(coupleCode));

            // When
            CoupleCode result = inviteCodeDomainService.getCoupleCodeByMemberId(memberId);

            // Then
            assertThat(result).isEqualTo(coupleCode);
            then(loadCoupleCodePort).should().loadCoupleCodeByMemberId(memberId);
        }

        @Test
        @DisplayName("실패: 존재하지 않는 멤버 ID로 조회 시 CoupleCodeNotFoundException이 발생한다")
        void givenNonExistentMemberId_whenGetCoupleCodeByMemberId_thenThrowCoupleCodeNotFoundException() {
            // Given
            MemberId memberId = MemberId.of(999L);

            given(loadCoupleCodePort.loadCoupleCodeByMemberId(memberId))
                    .willReturn(Optional.empty());

            // When & Then
            assertThatThrownBy(() -> inviteCodeDomainService.getCoupleCodeByMemberId(memberId))
                    .isInstanceOf(InviteCodeNotFoundException.class);

            then(loadCoupleCodePort).should().loadCoupleCodeByMemberId(memberId);
        }
    }

    @Nested
    @DisplayName("고유한 커플 코드 생성 및 저장 기능")
    class GenerateAndSaveUniqueCoupleCodeFeature {

        @Test
        @DisplayName("성공: 첫 번째 시도에서 고유한 커플 코드를 생성하고 저장한다")
        void givenValidMemberAndDate_whenGenerateAndSaveUniqueCoupleCode_thenReturnCoupleCode() {
            // Given
            Member member = mock(Member.class);
            LocalDate loveStartDate = LocalDate.of(2024, 1, 1);
            String generatedCode = "GENERATED123";
            CoupleCode coupleCode = mock(CoupleCode.class);

            given(generateInviteCodePort.generateInviteCode()).willReturn(generatedCode);
            given(member.generateCoupleCode(generatedCode, loveStartDate)).willReturn(coupleCode);
            given(saveCoupleCodePort.saveCoupleCode(coupleCode)).willReturn(coupleCode);

            // When
            CoupleCode result = inviteCodeDomainService.generateAndSaveUniqueCoupleCode(member, loveStartDate);

            // Then
            assertThat(result).isEqualTo(coupleCode);
            then(generateInviteCodePort).should().generateInviteCode();
            then(member).should().generateCoupleCode(generatedCode, loveStartDate);
            then(saveCoupleCodePort).should().saveCoupleCode(coupleCode);
        }

        @Test
        @DisplayName("성공: 중복 코드 발생 후 재시도하여 커플 코드를 생성하고 저장한다")
        void givenDuplicateCodeThenUnique_whenGenerateAndSaveUniqueCoupleCode_thenReturnCoupleCode() {
            // Given
            Member member = mock(Member.class);
            LocalDate loveStartDate = LocalDate.of(2024, 1, 1);
            String firstCode = "DUPLICATE123";
            String secondCode = "UNIQUE456";
            CoupleCode firstCoupleCode = mock(CoupleCode.class);
            CoupleCode secondCoupleCode = mock(CoupleCode.class);

            given(generateInviteCodePort.generateInviteCode())
                    .willReturn(firstCode)
                    .willReturn(secondCode);
            given(member.generateCoupleCode(firstCode, loveStartDate)).willReturn(firstCoupleCode);
            given(member.generateCoupleCode(secondCode, loveStartDate)).willReturn(secondCoupleCode);
            given(saveCoupleCodePort.saveCoupleCode(firstCoupleCode))
                    .willThrow(new DataIntegrityViolationException("Duplicate key"));
            given(saveCoupleCodePort.saveCoupleCode(secondCoupleCode)).willReturn(secondCoupleCode);

            // When
            CoupleCode result = inviteCodeDomainService.generateAndSaveUniqueCoupleCode(member, loveStartDate);

            // Then
            assertThat(result).isEqualTo(secondCoupleCode);
            then(generateInviteCodePort).should(times(2)).generateInviteCode();
            then(member).should().generateCoupleCode(firstCode, loveStartDate);
            then(member).should().generateCoupleCode(secondCode, loveStartDate);
            then(saveCoupleCodePort).should().saveCoupleCode(firstCoupleCode);
            then(saveCoupleCodePort).should().saveCoupleCode(secondCoupleCode);
        }

        @Test
        @DisplayName("실패: 최대 재시도 횟수 초과 시 InviteCodeGenerateFailedException이 발생한다")
        void givenMaxRetryExceeded_whenGenerateAndSaveUniqueCoupleCode_thenThrowInviteCodeGenerateFailedException() {
            // Given
            Member member = mock(Member.class);
            LocalDate loveStartDate = LocalDate.of(2024, 1, 1);
            String generatedCode = "DUPLICATE123";
            CoupleCode coupleCode = mock(CoupleCode.class);

            given(generateInviteCodePort.generateInviteCode()).willReturn(generatedCode);
            given(member.generateCoupleCode(generatedCode, loveStartDate)).willReturn(coupleCode);
            given(saveCoupleCodePort.saveCoupleCode(coupleCode))
                    .willThrow(new DataIntegrityViolationException("Duplicate key"));

            // When & Then
            assertThatThrownBy(() -> inviteCodeDomainService.generateAndSaveUniqueCoupleCode(member, loveStartDate))
                    .isInstanceOf(InviteCodeGenerateFailedException.class)
                    .hasMessage("커플 코드 생성에 실패했습니다. 재시도 횟수를 초과했습니다.");

            then(generateInviteCodePort).should(times(10)).generateInviteCode();
            then(member).should(times(10)).generateCoupleCode(generatedCode, loveStartDate);
            then(saveCoupleCodePort).should(times(10)).saveCoupleCode(coupleCode);
        }

        @Test
        @DisplayName("경계값: 오늘 날짜로 커플 코드를 생성한다")
        void givenTodayDate_whenGenerateAndSaveUniqueCoupleCode_thenReturnCoupleCode() {
            // Given
            Member member = mock(Member.class);
            LocalDate loveStartDate = LocalDate.now();
            String generatedCode = "TODAY123";
            CoupleCode coupleCode = mock(CoupleCode.class);

            given(generateInviteCodePort.generateInviteCode()).willReturn(generatedCode);
            given(member.generateCoupleCode(generatedCode, loveStartDate)).willReturn(coupleCode);
            given(saveCoupleCodePort.saveCoupleCode(coupleCode)).willReturn(coupleCode);

            // When
            CoupleCode result = inviteCodeDomainService.generateAndSaveUniqueCoupleCode(member, loveStartDate);

            // Then
            assertThat(result).isEqualTo(coupleCode);
            then(generateInviteCodePort).should().generateInviteCode();
            then(member).should().generateCoupleCode(generatedCode, loveStartDate);
            then(saveCoupleCodePort).should().saveCoupleCode(coupleCode);
        }

        @Test
        @DisplayName("경계값: 과거 날짜로 커플 코드를 생성한다")
        void givenPastDate_whenGenerateAndSaveUniqueCoupleCode_thenReturnCoupleCode() {
            // Given
            Member member = mock(Member.class);
            LocalDate loveStartDate = LocalDate.of(2020, 1, 1);
            String generatedCode = "PAST123";
            CoupleCode coupleCode = mock(CoupleCode.class);

            given(generateInviteCodePort.generateInviteCode()).willReturn(generatedCode);
            given(member.generateCoupleCode(generatedCode, loveStartDate)).willReturn(coupleCode);
            given(saveCoupleCodePort.saveCoupleCode(coupleCode)).willReturn(coupleCode);

            // When
            CoupleCode result = inviteCodeDomainService.generateAndSaveUniqueCoupleCode(member, loveStartDate);

            // Then
            assertThat(result).isEqualTo(coupleCode);
            then(generateInviteCodePort).should().generateInviteCode();
            then(member).should().generateCoupleCode(generatedCode, loveStartDate);
            then(saveCoupleCodePort).should().saveCoupleCode(coupleCode);
        }
    }
}