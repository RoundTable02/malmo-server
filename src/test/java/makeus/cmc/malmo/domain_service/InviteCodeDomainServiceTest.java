package makeus.cmc.malmo.domain_service;

import makeus.cmc.malmo.application.port.out.GenerateInviteCodePort;
import makeus.cmc.malmo.application.port.out.LoadInviteCodePort;
import makeus.cmc.malmo.application.port.out.LoadMemberPort;
import makeus.cmc.malmo.application.port.out.ValidateInviteCodePort;
import makeus.cmc.malmo.domain.exception.InviteCodeGenerateFailedException;
import makeus.cmc.malmo.domain.exception.InviteCodeNotFoundException;
import makeus.cmc.malmo.domain.exception.UsedInviteCodeException;
import makeus.cmc.malmo.domain.model.member.Member;
import makeus.cmc.malmo.domain.model.value.InviteCodeValue;
import makeus.cmc.malmo.domain.model.value.MemberId;
import makeus.cmc.malmo.domain.service.InviteCodeDomainService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("InviteCodeDomainService 단위 테스트")
class InviteCodeDomainServiceTest {

    @Mock
    private LoadMemberPort loadMemberPort;

    @Mock
    private LoadInviteCodePort loadInviteCodePort;

    @Mock
    private ValidateInviteCodePort validateInviteCodePort;

    @Mock
    private GenerateInviteCodePort generateInviteCodePort;

    @InjectMocks
    private InviteCodeDomainService inviteCodeDomainService;

    @Nested
    @DisplayName("초대 코드로 회원 조회 기능")
    class GetMemberByInviteCodeFeature {

        @Test
        @DisplayName("성공: 유효한 초대 코드로 회원을 조회한다")
        void givenValidInviteCode_whenGetMemberByInviteCode_thenReturnMember() {
            // Given
            InviteCodeValue inviteCodeValue = InviteCodeValue.of("INVITE123");
            Member member = mock(Member.class);

            given(loadMemberPort.loadMemberByInviteCode(inviteCodeValue))
                    .willReturn(Optional.of(member));

            // When
            Member result = inviteCodeDomainService.getMemberByInviteCode(inviteCodeValue);

            // Then
            assertThat(result).isEqualTo(member);
            then(loadMemberPort).should().loadMemberByInviteCode(inviteCodeValue);
        }

        @Test
        @DisplayName("실패: 존재하지 않는 초대 코드로 조회 시 InviteCodeNotFoundException이 발생한다")
        void givenNonExistentInviteCode_whenGetMemberByInviteCode_thenThrowInviteCodeNotFoundException() {
            // Given
            InviteCodeValue inviteCodeValue = InviteCodeValue.of("INVALID123");

            given(loadMemberPort.loadMemberByInviteCode(inviteCodeValue))
                    .willReturn(Optional.empty());

            // When & Then
            assertThatThrownBy(() -> inviteCodeDomainService.getMemberByInviteCode(inviteCodeValue))
                    .isInstanceOf(InviteCodeNotFoundException.class);

            then(loadMemberPort).should().loadMemberByInviteCode(inviteCodeValue);
        }
    }

    @Nested
    @DisplayName("멤버 ID로 초대 코드 조회 기능")
    class GetInviteCodeByMemberIdFeature {

        @Test
        @DisplayName("성공: 유효한 멤버 ID로 초대 코드를 조회한다")
        void givenValidMemberId_whenGetInviteCodeByMemberId_thenReturnInviteCode() {
            // Given
            MemberId memberId = MemberId.of(1L);
            InviteCodeValue inviteCodeValue = InviteCodeValue.of("INVITE123");

            given(loadInviteCodePort.loadInviteCodeByMemberId(memberId))
                    .willReturn(Optional.of(inviteCodeValue));

            // When
            InviteCodeValue result = inviteCodeDomainService.getInviteCodeByMemberId(memberId);

            // Then
            assertThat(result).isEqualTo(inviteCodeValue);
            then(loadInviteCodePort).should().loadInviteCodeByMemberId(memberId);
        }

        @Test
        @DisplayName("실패: 존재하지 않는 멤버 ID로 조회 시 InviteCodeNotFoundException이 발생한다")
        void givenNonExistentMemberId_whenGetInviteCodeByMemberId_thenThrowInviteCodeNotFoundException() {
            // Given
            MemberId memberId = MemberId.of(999L);

            given(loadInviteCodePort.loadInviteCodeByMemberId(memberId))
                    .willReturn(Optional.empty());

            // When & Then
            assertThatThrownBy(() -> inviteCodeDomainService.getInviteCodeByMemberId(memberId))
                    .isInstanceOf(InviteCodeNotFoundException.class);

            then(loadInviteCodePort).should().loadInviteCodeByMemberId(memberId);
        }
    }

    @Nested
    @DisplayName("고유한 초대 코드 생성 기능")
    class GenerateUniqueInviteCodeFeature {

        @Test
        @DisplayName("성공: 첫 번째 시도에서 고유한 초대 코드를 생성한다")
        void givenFirstAttempt_whenGenerateUniqueInviteCode_thenReturnInviteCode() {
            // Given
            String generatedCode = "UNIQUE123";
            InviteCodeValue inviteCodeValue = InviteCodeValue.of(generatedCode);

            given(generateInviteCodePort.generateInviteCode()).willReturn(generatedCode);
            given(validateInviteCodePort.validateDuplicateInviteCode(inviteCodeValue)).willReturn(false);

            // When
            InviteCodeValue result = inviteCodeDomainService.generateUniqueInviteCode();

            // Then
            assertThat(result).isEqualTo(inviteCodeValue);
            then(generateInviteCodePort).should().generateInviteCode();
            then(validateInviteCodePort).should().validateDuplicateInviteCode(inviteCodeValue);
        }

        @Test
        @DisplayName("성공: 중복 코드 발생 후 재시도하여 고유한 초대 코드를 생성한다")
        void givenDuplicateCodeThenUnique_whenGenerateUniqueInviteCode_thenReturnInviteCode() {
            // Given
            String firstCode = "DUPLICATE123";
            String secondCode = "UNIQUE456";
            InviteCodeValue firstInviteCode = InviteCodeValue.of(firstCode);
            InviteCodeValue secondInviteCode = InviteCodeValue.of(secondCode);

            given(generateInviteCodePort.generateInviteCode())
                    .willReturn(firstCode)
                    .willReturn(secondCode);
            given(validateInviteCodePort.validateDuplicateInviteCode(firstInviteCode)).willReturn(true);
            given(validateInviteCodePort.validateDuplicateInviteCode(secondInviteCode)).willReturn(false);

            // When
            InviteCodeValue result = inviteCodeDomainService.generateUniqueInviteCode();

            // Then
            assertThat(result).isEqualTo(secondInviteCode);
            then(generateInviteCodePort).should(times(2)).generateInviteCode();
            then(validateInviteCodePort).should().validateDuplicateInviteCode(firstInviteCode);
            then(validateInviteCodePort).should().validateDuplicateInviteCode(secondInviteCode);
        }

        @Test
        @DisplayName("실패: 최대 재시도 횟수 초과 시 InviteCodeGenerateFailedException이 발생한다")
        void givenMaxRetryExceeded_whenGenerateUniqueInviteCode_thenThrowInviteCodeGenerateFailedException() {
            // Given
            String generatedCode = "DUPLICATE123";
            InviteCodeValue inviteCodeValue = InviteCodeValue.of(generatedCode);

            given(generateInviteCodePort.generateInviteCode()).willReturn(generatedCode);
            given(validateInviteCodePort.validateDuplicateInviteCode(inviteCodeValue)).willReturn(true);

            // When & Then
            assertThatThrownBy(() -> inviteCodeDomainService.generateUniqueInviteCode())
                    .isInstanceOf(InviteCodeGenerateFailedException.class)
                    .hasMessage("초대 코드 생성에 실패했습니다. 재시도 횟수를 초과했습니다.");

            then(generateInviteCodePort).should(times(10)).generateInviteCode();
            then(validateInviteCodePort).should(times(10)).validateDuplicateInviteCode(inviteCodeValue);
        }
    }

    @Nested
    @DisplayName("사용된 초대 코드 검증 기능")
    class ValidateUsedInviteCodeFeature {

        @Test
        @DisplayName("성공: 사용되지 않은 초대 코드는 검증을 통과한다")
        void givenUnusedInviteCode_whenValidateUsedInviteCode_thenNoException() {
            // Given
            InviteCodeValue inviteCodeValue = InviteCodeValue.of("UNUSED123");

            given(validateInviteCodePort.isAlreadyCoupleMemberByInviteCode(inviteCodeValue)).willReturn(false);

            // When & Then (예외가 발생하지 않아야 함)
            inviteCodeDomainService.validateUsedInviteCode(inviteCodeValue);

            then(validateInviteCodePort).should().isAlreadyCoupleMemberByInviteCode(inviteCodeValue);
        }

        @Test
        @DisplayName("실패: 이미 사용된 초대 코드는 UsedInviteCodeException이 발생한다")
        void givenUsedInviteCode_whenValidateUsedInviteCode_thenThrowUsedInviteCodeException() {
            // Given
            InviteCodeValue inviteCodeValue = InviteCodeValue.of("USED123");

            given(validateInviteCodePort.isAlreadyCoupleMemberByInviteCode(inviteCodeValue)).willReturn(true);

            // When & Then
            assertThatThrownBy(() -> inviteCodeDomainService.validateUsedInviteCode(inviteCodeValue))
                    .isInstanceOf(UsedInviteCodeException.class)
                    .hasMessage("이미 사용된 커플 코드입니다. 다른 코드를 입력해주세요.");

            then(validateInviteCodePort).should().isAlreadyCoupleMemberByInviteCode(inviteCodeValue);
        }
    }
}