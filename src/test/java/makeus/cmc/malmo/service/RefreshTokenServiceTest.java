package makeus.cmc.malmo.service;

import makeus.cmc.malmo.adaptor.out.jwt.TokenInfo;
import makeus.cmc.malmo.domain.exception.MemberNotFoundException;
import makeus.cmc.malmo.application.exception.InvalidRefreshTokenException;
import makeus.cmc.malmo.application.port.in.RefreshTokenUseCase;
import makeus.cmc.malmo.application.port.out.GenerateTokenPort;
import makeus.cmc.malmo.application.port.out.SaveMemberPort;
import makeus.cmc.malmo.application.port.out.ValidateTokenPort;
import makeus.cmc.malmo.application.service.RefreshTokenService;
import makeus.cmc.malmo.domain.model.member.Member;
import makeus.cmc.malmo.domain.model.member.MemberRole;
import makeus.cmc.malmo.domain.model.value.MemberId;
import makeus.cmc.malmo.domain.service.MemberDomainService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("RefreshTokenService 단위 테스트")
class RefreshTokenServiceTest {

    @Mock
    private MemberDomainService memberDomainService;

    @Mock
    private SaveMemberPort saveMemberPort;

    @Mock
    private GenerateTokenPort generateTokenPort;

    @Mock
    private ValidateTokenPort validateTokenPort;

    @InjectMocks
    private RefreshTokenService refreshTokenService;

    @Nested
    @DisplayName("토큰 갱신 기능")
    class RefreshTokenFeature {

        @Test
        @DisplayName("성공: 유효한 리프레시 토큰으로 토큰 갱신이 성공한다")
        void givenValidRefreshToken_whenRefreshToken_thenReturnTokenResponse() {
            // Given
            String refreshToken = "validRefreshToken123";
            String memberId = "1";
            Long memberIdLong = 1L;
            String newAccessToken = "newAccessToken123";
            String newRefreshToken = "newRefreshToken123";
            String grantType = "Bearer";

            RefreshTokenUseCase.RefreshTokenCommand command = RefreshTokenUseCase.RefreshTokenCommand.builder()
                    .refreshToken(refreshToken)
                    .build();

            Member member = mock(Member.class);
            given(member.getId()).willReturn(memberIdLong);
            given(member.getMemberRole()).willReturn(MemberRole.MEMBER);
            given(member.getRefreshToken()).willReturn(refreshToken);

            TokenInfo tokenInfo = mock(TokenInfo.class);
            given(tokenInfo.getGrantType()).willReturn(grantType);
            given(tokenInfo.getAccessToken()).willReturn(newAccessToken);
            given(tokenInfo.getRefreshToken()).willReturn(newRefreshToken);

            given(validateTokenPort.validateToken(refreshToken)).willReturn(true);
            given(validateTokenPort.getMemberIdFromToken(refreshToken)).willReturn(memberId);
            given(memberDomainService.getMemberById(MemberId.of(memberIdLong))).willReturn(member);
            given(generateTokenPort.generateToken(memberIdLong, MemberRole.MEMBER)).willReturn(tokenInfo);
            given(saveMemberPort.saveMember(member)).willReturn(member);

            // When
            RefreshTokenUseCase.TokenResponse response = refreshTokenService.refreshToken(command);

            // Then
            assertThat(response).isNotNull();
            assertThat(response.getGrantType()).isEqualTo(grantType);
            assertThat(response.getAccessToken()).isEqualTo(newAccessToken);
            assertThat(response.getRefreshToken()).isEqualTo(newRefreshToken);

            then(validateTokenPort).should().validateToken(refreshToken);
            then(validateTokenPort).should().getMemberIdFromToken(refreshToken);
            then(memberDomainService).should().getMemberById(MemberId.of(memberIdLong));
            then(generateTokenPort).should().generateToken(memberIdLong, MemberRole.MEMBER);
            then(member).should().refreshMemberToken(newRefreshToken);
            then(saveMemberPort).should().saveMember(member);
        }

        @Test
        @DisplayName("실패: 유효하지 않은 리프레시 토큰으로 토큰 갱신 시 InvalidRefreshTokenException이 발생한다")
        void givenInvalidRefreshToken_whenRefreshToken_thenThrowInvalidRefreshTokenException() {
            // Given
            String invalidRefreshToken = "invalidRefreshToken123";

            RefreshTokenUseCase.RefreshTokenCommand command = RefreshTokenUseCase.RefreshTokenCommand.builder()
                    .refreshToken(invalidRefreshToken)
                    .build();

            given(validateTokenPort.validateToken(invalidRefreshToken)).willReturn(false);

            // When & Then
            assertThatThrownBy(() -> refreshTokenService.refreshToken(command))
                    .isInstanceOf(InvalidRefreshTokenException.class);

            then(validateTokenPort).should().validateToken(invalidRefreshToken);
            then(validateTokenPort).should(never()).getMemberIdFromToken(any());
            then(memberDomainService).should(never()).getMemberById(any());
            then(generateTokenPort).should(never()).generateToken(any(), any());
            then(saveMemberPort).should(never()).saveMember(any());
        }

        @Test
        @DisplayName("실패: 존재하지 않는 사용자의 토큰으로 갱신 시 MemberNotFoundException이 발생한다")
        void givenNonExistentMember_whenRefreshToken_thenThrowMemberNotFoundException() {
            // Given
            String refreshToken = "validRefreshToken123";
            String memberId = "999";
            Long memberIdLong = 999L;

            RefreshTokenUseCase.RefreshTokenCommand command = RefreshTokenUseCase.RefreshTokenCommand.builder()
                    .refreshToken(refreshToken)
                    .build();

            given(validateTokenPort.validateToken(refreshToken)).willReturn(true);
            given(validateTokenPort.getMemberIdFromToken(refreshToken)).willReturn(memberId);
            given(memberDomainService.getMemberById(MemberId.of(memberIdLong)))
                    .willThrow(new MemberNotFoundException());

            // When & Then
            assertThatThrownBy(() -> refreshTokenService.refreshToken(command))
                    .isInstanceOf(MemberNotFoundException.class);

            then(validateTokenPort).should().validateToken(refreshToken);
            then(validateTokenPort).should().getMemberIdFromToken(refreshToken);
            then(memberDomainService).should().getMemberById(MemberId.of(memberIdLong));
            then(generateTokenPort).should(never()).generateToken(any(), any());
            then(saveMemberPort).should(never()).saveMember(any());
        }

        @Test
        @DisplayName("실패: 저장된 리프레시 토큰과 일치하지 않을 때 InvalidRefreshTokenException이 발생한다")
        void givenMismatchedRefreshToken_whenRefreshToken_thenThrowInvalidRefreshTokenException() {
            // Given
            String refreshToken = "providedRefreshToken123";
            String storedRefreshToken = "storedRefreshToken456";
            String memberId = "1";
            Long memberIdLong = 1L;

            RefreshTokenUseCase.RefreshTokenCommand command = RefreshTokenUseCase.RefreshTokenCommand.builder()
                    .refreshToken(refreshToken)
                    .build();

            Member member = mock(Member.class);
            given(member.getRefreshToken()).willReturn(storedRefreshToken);

            given(validateTokenPort.validateToken(refreshToken)).willReturn(true);
            given(validateTokenPort.getMemberIdFromToken(refreshToken)).willReturn(memberId);
            given(memberDomainService.getMemberById(MemberId.of(memberIdLong))).willReturn(member);

            // When & Then
            assertThatThrownBy(() -> refreshTokenService.refreshToken(command))
                    .isInstanceOf(InvalidRefreshTokenException.class);

            then(validateTokenPort).should().validateToken(refreshToken);
            then(validateTokenPort).should().getMemberIdFromToken(refreshToken);
            then(memberDomainService).should().getMemberById(MemberId.of(memberIdLong));
            then(generateTokenPort).should(never()).generateToken(any(), any());
            then(saveMemberPort).should(never()).saveMember(any());
        }
    }
}
