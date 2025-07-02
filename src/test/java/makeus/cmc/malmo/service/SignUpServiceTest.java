package makeus.cmc.malmo.service;

import makeus.cmc.malmo.adaptor.out.persistence.exception.InviteCodeGenerateFailedException;
import makeus.cmc.malmo.adaptor.out.persistence.exception.MemberNotFoundException;
import makeus.cmc.malmo.adaptor.out.persistence.exception.TermsNotFoundException;
import makeus.cmc.malmo.application.port.in.SignUpUseCase;
import makeus.cmc.malmo.application.port.out.*;
import makeus.cmc.malmo.application.service.SignUpService;
import makeus.cmc.malmo.domain.model.member.CoupleCode;
import makeus.cmc.malmo.domain.model.member.Member;
import makeus.cmc.malmo.domain.model.terms.MemberTermsAgreement;
import makeus.cmc.malmo.domain.model.terms.Terms;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("SignUpService 단위 테스트")
class SignUpServiceTest {

    @Mock
    private LoadMemberPort loadMemberPort;

    @Mock
    private SaveMemberPort saveMemberPort;

    @Mock
    private LoadTermsPort loadTermsPort;

    @Mock
    private SaveMemberTermsAgreement saveMemberTermsAgreement;

    @Mock
    private GenerateInviteCodePort generateInviteCodePort;

    @Mock
    private SaveCoupleCodePort saveCoupleCodePort;

    @InjectMocks
    private SignUpService signUpService;

    @Nested
    @DisplayName("회원가입 기능")
    class SignUpFeature {

        @Test
        @DisplayName("성공: 유효한 회원정보와 약관동의로 회원가입이 성공한다")
        void givenValidMemberInfoAndTermsAgreement_whenSignUp_thenReturnSignUpResponse() {
            // Given
            Long memberId = 1L;
            String nickname = "테스트닉네임";
            LocalDate loveStartDate = LocalDate.of(2024, 1, 1);
            String expectedInviteCode = "ABC123";

            SignUpUseCase.TermsCommand termsCommand1 = SignUpUseCase.TermsCommand.builder()
                    .termsId(1L)
                    .isAgreed(true)
                    .build();

            SignUpUseCase.TermsCommand termsCommand2 = SignUpUseCase.TermsCommand.builder()
                    .termsId(2L)
                    .isAgreed(true)
                    .build();

            SignUpUseCase.SignUpCommand command = SignUpUseCase.SignUpCommand.builder()
                    .memberId(memberId)
                    .nickname(nickname)
                    .loveStartDate(loveStartDate)
                    .terms(Arrays.asList(termsCommand1, termsCommand2))
                    .build();

            Member member = mock(Member.class);
            Member savedMember = mock(Member.class);
            Terms terms1 = mock(Terms.class);
            Terms terms2 = mock(Terms.class);
            CoupleCode coupleCode = mock(CoupleCode.class);

            given(loadMemberPort.loadMemberById(memberId)).willReturn(Optional.of(member));
            given(saveMemberPort.saveMember(member)).willReturn(savedMember);
            given(savedMember.getId()).willReturn(memberId);
            given(loadTermsPort.loadTermsById(1L)).willReturn(Optional.of(terms1));
            given(loadTermsPort.loadTermsById(2L)).willReturn(Optional.of(terms2));
            given(terms1.getId()).willReturn(1L);
            given(terms2.getId()).willReturn(2L);
            given(generateInviteCodePort.generateInviteCode()).willReturn(expectedInviteCode);
            given(savedMember.generateCoupleCode(expectedInviteCode, loveStartDate)).willReturn(coupleCode);

            // When
            SignUpUseCase.SignUpResponse response = signUpService.signUp(command);

            // Then
            assertThat(response).isNotNull();
            assertThat(response.getCoupleCode()).isEqualTo(expectedInviteCode);

            then(loadMemberPort).should().loadMemberById(memberId);
            then(member).should().signUp(nickname);
            then(saveMemberPort).should().saveMember(member);
            then(loadTermsPort).should().loadTermsById(1L);
            then(loadTermsPort).should().loadTermsById(2L);
            then(saveMemberTermsAgreement).should().saveMemberTermsAgreement(any(MemberTermsAgreement.class), eq(member), eq(terms1));
            then(saveMemberTermsAgreement).should().saveMemberTermsAgreement(any(MemberTermsAgreement.class), eq(member), eq(terms2));
            then(generateInviteCodePort).should().generateInviteCode();
            then(savedMember).should().generateCoupleCode(expectedInviteCode, loveStartDate);
            then(saveCoupleCodePort).should().saveCoupleCode(coupleCode);
        }

        @Test
        @DisplayName("실패: 존재하지 않는 회원으로 회원가입 시 MemberNotFoundException이 발생한다")
        void givenNonExistentMember_whenSignUp_thenThrowMemberNotFoundException() {
            // Given
            Long memberId = 999L;
            String nickname = "테스트닉네임";
            LocalDate loveStartDate = LocalDate.of(2024, 1, 1);

            SignUpUseCase.SignUpCommand command = SignUpUseCase.SignUpCommand.builder()
                    .memberId(memberId)
                    .nickname(nickname)
                    .loveStartDate(loveStartDate)
                    .terms(Arrays.asList())
                    .build();

            given(loadMemberPort.loadMemberById(memberId)).willReturn(Optional.empty());

            // When & Then
            assertThatThrownBy(() -> signUpService.signUp(command))
                    .isInstanceOf(MemberNotFoundException.class);

            then(loadMemberPort).should().loadMemberById(memberId);
            then(saveMemberPort).should(never()).saveMember(any());
            then(generateInviteCodePort).should(never()).generateInviteCode();
            then(saveCoupleCodePort).should(never()).saveCoupleCode(any());
        }

        @Test
        @DisplayName("실패: 존재하지 않는 약관으로 회원가입 시 TermsNotFoundException이 발생한다")
        void givenNonExistentTerms_whenSignUp_thenThrowTermsNotFoundException() {
            // Given
            Long memberId = 1L;
            String nickname = "테스트닉네임";
            LocalDate loveStartDate = LocalDate.of(2024, 1, 1);

            SignUpUseCase.TermsCommand termsCommand = SignUpUseCase.TermsCommand.builder()
                    .termsId(999L)
                    .isAgreed(true)
                    .build();

            SignUpUseCase.SignUpCommand command = SignUpUseCase.SignUpCommand.builder()
                    .memberId(memberId)
                    .nickname(nickname)
                    .loveStartDate(loveStartDate)
                    .terms(Arrays.asList(termsCommand))
                    .build();

            Member member = mock(Member.class);
            Member savedMember = mock(Member.class);

            given(loadMemberPort.loadMemberById(memberId)).willReturn(Optional.of(member));
            given(saveMemberPort.saveMember(member)).willReturn(savedMember);
            given(loadTermsPort.loadTermsById(999L)).willReturn(Optional.empty());

            // When & Then
            assertThatThrownBy(() -> signUpService.signUp(command))
                    .isInstanceOf(TermsNotFoundException.class);

            then(loadMemberPort).should().loadMemberById(memberId);
            then(member).should().signUp(nickname);
            then(saveMemberPort).should().saveMember(member);
            then(loadTermsPort).should().loadTermsById(999L);
            then(saveMemberTermsAgreement).should(never()).saveMemberTermsAgreement(any(), any(), any());
            then(generateInviteCodePort).should(never()).generateInviteCode();
            then(saveCoupleCodePort).should(never()).saveCoupleCode(any());
        }

        @Test
        @DisplayName("성공: 커플 코드 생성 중 중복 발생 시 재시도 후 성공한다")
        void givenDuplicateInviteCodeThenUniqueCode_whenSignUp_thenRetryAndReturnSignUpResponse() {
            // Given
            Long memberId = 1L;
            String nickname = "테스트닉네임";
            LocalDate loveStartDate = LocalDate.of(2024, 1, 1);
            String duplicateInviteCode = "DUPLICATE";
            String uniqueInviteCode = "UNIQUE123";

            SignUpUseCase.TermsCommand termsCommand = SignUpUseCase.TermsCommand.builder()
                    .termsId(1L)
                    .isAgreed(true)
                    .build();

            SignUpUseCase.SignUpCommand command = SignUpUseCase.SignUpCommand.builder()
                    .memberId(memberId)
                    .nickname(nickname)
                    .loveStartDate(loveStartDate)
                    .terms(Arrays.asList(termsCommand))
                    .build();

            Member member = mock(Member.class);
            Member savedMember = mock(Member.class);
            Terms terms = mock(Terms.class);
            CoupleCode duplicateCoupleCode = mock(CoupleCode.class);
            CoupleCode uniqueCoupleCode = mock(CoupleCode.class);

            given(loadMemberPort.loadMemberById(memberId)).willReturn(Optional.of(member));
            given(saveMemberPort.saveMember(member)).willReturn(savedMember);
            given(savedMember.getId()).willReturn(memberId);
            given(loadTermsPort.loadTermsById(1L)).willReturn(Optional.of(terms));
            given(terms.getId()).willReturn(1L);
            given(generateInviteCodePort.generateInviteCode())
                    .willReturn(duplicateInviteCode)
                    .willReturn(uniqueInviteCode);
            given(savedMember.generateCoupleCode(duplicateInviteCode, loveStartDate)).willReturn(duplicateCoupleCode);
            given(savedMember.generateCoupleCode(uniqueInviteCode, loveStartDate)).willReturn(uniqueCoupleCode);

            // 첫 번째 saveCoupleCode 호출 시 DataIntegrityViolationException 발생, 두 번째 호출 시 성공
            doThrow(new DataIntegrityViolationException("Duplicate key")).when(saveCoupleCodePort).saveCoupleCode(duplicateCoupleCode);

            // When
            SignUpUseCase.SignUpResponse response = signUpService.signUp(command);

            // Then
            assertThat(response).isNotNull();
            assertThat(response.getCoupleCode()).isEqualTo(uniqueInviteCode);

            then(loadMemberPort).should().loadMemberById(memberId);
            then(member).should().signUp(nickname);
            then(saveMemberPort).should().saveMember(member);
            then(loadTermsPort).should().loadTermsById(1L);
            then(saveMemberTermsAgreement).should().saveMemberTermsAgreement(any(MemberTermsAgreement.class), eq(member), eq(terms));
            then(generateInviteCodePort).should(times(2)).generateInviteCode();
            then(savedMember).should().generateCoupleCode(duplicateInviteCode, loveStartDate);
            then(savedMember).should().generateCoupleCode(uniqueInviteCode, loveStartDate);
            then(saveCoupleCodePort).should().saveCoupleCode(duplicateCoupleCode);
            then(saveCoupleCodePort).should().saveCoupleCode(uniqueCoupleCode);
        }

        @Test
        @DisplayName("실패: 커플 코드 생성 재시도 횟수 초과 시 InviteCodeGenerateFailedException이 발생한다")
        void givenMaxRetryExceeded_whenSignUp_thenThrowInviteCodeGenerateFailedException() {
            // Given
            Long memberId = 1L;
            String nickname = "테스트닉네임";
            LocalDate loveStartDate = LocalDate.of(2024, 1, 1);
            String duplicateInviteCode = "DUPLICATE";

            SignUpUseCase.TermsCommand termsCommand = SignUpUseCase.TermsCommand.builder()
                    .termsId(1L)
                    .isAgreed(true)
                    .build();

            SignUpUseCase.SignUpCommand command = SignUpUseCase.SignUpCommand.builder()
                    .memberId(memberId)
                    .nickname(nickname)
                    .loveStartDate(loveStartDate)
                    .terms(Arrays.asList(termsCommand))
                    .build();

            Member member = mock(Member.class);
            Member savedMember = mock(Member.class);
            Terms terms = mock(Terms.class);
            CoupleCode duplicateCoupleCode = mock(CoupleCode.class);

            given(loadMemberPort.loadMemberById(memberId)).willReturn(Optional.of(member));
            given(saveMemberPort.saveMember(member)).willReturn(savedMember);
            given(savedMember.getId()).willReturn(memberId);
            given(loadTermsPort.loadTermsById(1L)).willReturn(Optional.of(terms));
            given(terms.getId()).willReturn(1L);
            given(generateInviteCodePort.generateInviteCode()).willReturn(duplicateInviteCode);
            given(savedMember.generateCoupleCode(duplicateInviteCode, loveStartDate)).willReturn(duplicateCoupleCode);
            
            // 모든 시도에서 DataIntegrityViolationException 발생
            doThrow(new DataIntegrityViolationException("Duplicate key")).when(saveCoupleCodePort).saveCoupleCode(duplicateCoupleCode);

            // When & Then
            assertThatThrownBy(() -> signUpService.signUp(command))
                    .isInstanceOf(InviteCodeGenerateFailedException.class)
                    .hasMessage("커플 코드 생성에 실패했습니다. 재시도 횟수를 초과했습니다.");

            then(loadMemberPort).should().loadMemberById(memberId);
            then(member).should().signUp(nickname);
            then(saveMemberPort).should().saveMember(member);
            then(loadTermsPort).should().loadTermsById(1L);
            then(saveMemberTermsAgreement).should().saveMemberTermsAgreement(any(MemberTermsAgreement.class), eq(member), eq(terms));
            then(generateInviteCodePort).should(times(10)).generateInviteCode();
            then(savedMember).should(times(10)).generateCoupleCode(duplicateInviteCode, loveStartDate);
            then(saveCoupleCodePort).should(times(10)).saveCoupleCode(duplicateCoupleCode);
        }
    }
}