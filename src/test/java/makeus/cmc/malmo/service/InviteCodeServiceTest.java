package makeus.cmc.malmo.service;

import makeus.cmc.malmo.domain.exception.InviteCodeNotFoundException;
import makeus.cmc.malmo.application.port.in.GetInviteCodeUseCase;
import makeus.cmc.malmo.application.service.InviteCodeService;
import makeus.cmc.malmo.domain.value.id.InviteCodeValue;
import makeus.cmc.malmo.domain.value.id.MemberId;
import makeus.cmc.malmo.domain.service.InviteCodeDomainService;
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
@DisplayName("InviteCodeService 단위 테스트")
class InviteCodeServiceTest {

    @Mock
    private InviteCodeDomainService inviteCodeDomainService;

    @InjectMocks
    private InviteCodeService inviteCodeService;

    @Nested
    @DisplayName("초대 코드 조회 기능")
    class GetInviteCodeFeature {

        @Test
        @DisplayName("성공: 유효한 사용자 ID로 초대 코드 조회가 성공한다")
        void givenValidUserId_whenGetInviteCode_thenReturnInviteCodeResponse() {
            // Given
            Long userId = 1L;
            String expectedInviteCode = "INVITE123";

            GetInviteCodeUseCase.InviteCodeCommand command = GetInviteCodeUseCase.InviteCodeCommand.builder()
                    .userId(userId)
                    .build();

            InviteCodeValue inviteCodeValue = InviteCodeValue.of(expectedInviteCode);

            given(inviteCodeDomainService.getInviteCodeByMemberId(MemberId.of(userId)))
                    .willReturn(inviteCodeValue);

            // When
            GetInviteCodeUseCase.InviteCodeResponseDto response = inviteCodeService.getInviteCode(command);

            // Then
            assertThat(response).isNotNull();
            assertThat(response.getCoupleCode()).isEqualTo(expectedInviteCode);

            then(inviteCodeDomainService).should().getInviteCodeByMemberId(MemberId.of(userId));
        }

        @Test
        @DisplayName("실패: 존재하지 않는 사용자 ID로 초대 코드 조회 시 InviteCodeNotFoundException이 발생한다")
        void givenNonExistentUserId_whenGetInviteCode_thenThrowInviteCodeNotFoundException() {
            // Given
            Long userId = 999L;

            GetInviteCodeUseCase.InviteCodeCommand command = GetInviteCodeUseCase.InviteCodeCommand.builder()
                    .userId(userId)
                    .build();

            given(inviteCodeDomainService.getInviteCodeByMemberId(MemberId.of(userId)))
                    .willThrow(new InviteCodeNotFoundException());

            // When & Then
            assertThatThrownBy(() -> inviteCodeService.getInviteCode(command))
                    .isInstanceOf(InviteCodeNotFoundException.class);

            then(inviteCodeDomainService).should().getInviteCodeByMemberId(MemberId.of(userId));
        }
    }
}