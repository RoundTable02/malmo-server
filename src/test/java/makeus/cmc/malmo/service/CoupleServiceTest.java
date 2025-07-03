package makeus.cmc.malmo.service;

import makeus.cmc.malmo.adaptor.out.persistence.exception.CoupleCodeNotFoundException;
import makeus.cmc.malmo.adaptor.out.persistence.exception.MemberNotFoundException;
import makeus.cmc.malmo.application.port.in.CoupleLinkUseCase;
import makeus.cmc.malmo.application.port.out.LoadCoupleCodePort;
import makeus.cmc.malmo.application.port.out.LoadMemberPort;
import makeus.cmc.malmo.application.port.out.SaveCouplePort;
import makeus.cmc.malmo.application.service.CoupleService;
import makeus.cmc.malmo.domain.model.couple.Couple;
import makeus.cmc.malmo.domain.model.member.CoupleCode;
import makeus.cmc.malmo.domain.model.member.Member;
import makeus.cmc.malmo.domain.model.value.MemberId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CoupleService 단위 테스트")
class CoupleServiceTest {

    @Mock
    private LoadMemberPort loadMemberPort;

    @Mock
    private LoadCoupleCodePort loadCoupleCodePort;

    @Mock
    private SaveCouplePort saveCouplePort;

    @InjectMocks
    private CoupleService coupleService;

    @Nested
    @DisplayName("커플 연결 기능")
    class CoupleLinkFeature {

        @Test
        @DisplayName("성공: 유효한 사용자와 커플 코드로 커플 연결이 성공한다")
        void givenValidUserAndCoupleCode_whenCoupleLink_thenReturnCoupleLinkResponse() {
            // Given
            Long userId = 1L;
            String inviteCode = "INVITE123";
            Long coupleId = 10L;
            LocalDate startLoveDate = LocalDate.of(2024, 1, 1);

            CoupleLinkUseCase.CoupleLinkCommand command = CoupleLinkUseCase.CoupleLinkCommand.builder()
                    .userId(userId)
                    .coupleCode(inviteCode)
                    .build();

            Member member = mock(Member.class);
            given(member.getId()).willReturn(userId);

            MemberId memberId = mock(MemberId.class);
            given(memberId.getValue()).willReturn(2L);

            CoupleCode coupleCode = mock(CoupleCode.class);
            given(coupleCode.getMemberId()).willReturn(memberId);
            given(coupleCode.getStartLoveDate()).willReturn(startLoveDate);

            Couple savedCouple = mock(Couple.class);
            given(savedCouple.getId()).willReturn(coupleId);

            given(loadMemberPort.loadMemberById(userId)).willReturn(Optional.of(member));
            given(loadCoupleCodePort.loadCoupleCodeByInviteCode(inviteCode)).willReturn(Optional.of(coupleCode));
            given(saveCouplePort.saveCouple(any(Couple.class))).willReturn(savedCouple);

            // When
            CoupleLinkUseCase.CoupleLinkResponse response = coupleService.coupleLink(command);

            // Then
            assertThat(response).isNotNull();
            assertThat(response.getCoupleId()).isEqualTo(coupleId);

            then(loadMemberPort).should().loadMemberById(userId);
            then(loadCoupleCodePort).should().loadCoupleCodeByInviteCode(inviteCode);
            then(saveCouplePort).should().saveCouple(any(Couple.class));
        }

        @Test
        @DisplayName("실패: 존재하지 않는 사용자로 커플 연결 시 MemberNotFoundException이 발생한다")
        void givenNonExistentUser_whenCoupleLink_thenThrowMemberNotFoundException() {
            // Given
            Long userId = 999L;
            String inviteCode = "INVITE123";

            CoupleLinkUseCase.CoupleLinkCommand command = CoupleLinkUseCase.CoupleLinkCommand.builder()
                    .userId(userId)
                    .coupleCode(inviteCode)
                    .build();

            given(loadMemberPort.loadMemberById(userId)).willReturn(Optional.empty());

            // When & Then
            assertThatThrownBy(() -> coupleService.coupleLink(command))
                    .isInstanceOf(MemberNotFoundException.class);

            then(loadMemberPort).should().loadMemberById(userId);
            then(loadCoupleCodePort).should(never()).loadCoupleCodeByInviteCode(any());
            then(saveCouplePort).should(never()).saveCouple(any());
        }

        @Test
        @DisplayName("실패: 존재하지 않는 커플 코드로 커플 연결 시 CoupleCodeNotFoundException이 발생한다")
        void givenNonExistentCoupleCode_whenCoupleLink_thenThrowCoupleCodeNotFoundException() {
            // Given
            Long userId = 1L;
            String invalidInviteCode = "INVALID123";

            CoupleLinkUseCase.CoupleLinkCommand command = CoupleLinkUseCase.CoupleLinkCommand.builder()
                    .userId(userId)
                    .coupleCode(invalidInviteCode)
                    .build();

            Member member = mock(Member.class);

            given(loadMemberPort.loadMemberById(userId)).willReturn(Optional.of(member));
            given(loadCoupleCodePort.loadCoupleCodeByInviteCode(invalidInviteCode)).willReturn(Optional.empty());

            // When & Then
            assertThatThrownBy(() -> coupleService.coupleLink(command))
                    .isInstanceOf(CoupleCodeNotFoundException.class);

            then(loadMemberPort).should().loadMemberById(userId);
            then(loadCoupleCodePort).should().loadCoupleCodeByInviteCode(invalidInviteCode);
            then(saveCouplePort).should(never()).saveCouple(any());
        }
    }
}
