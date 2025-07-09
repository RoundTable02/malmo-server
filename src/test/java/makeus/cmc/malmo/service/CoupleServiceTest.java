package makeus.cmc.malmo.service;

import makeus.cmc.malmo.application.port.out.SendSseEventPort;
import makeus.cmc.malmo.domain.exception.InviteCodeNotFoundException;
import makeus.cmc.malmo.domain.exception.MemberNotFoundException;
import makeus.cmc.malmo.application.port.in.CoupleLinkUseCase;
import makeus.cmc.malmo.application.port.out.SaveCouplePort;
import makeus.cmc.malmo.application.service.CoupleService;
import makeus.cmc.malmo.domain.model.couple.Couple;
import makeus.cmc.malmo.domain.model.member.Member;
import makeus.cmc.malmo.domain.model.value.InviteCodeValue;
import makeus.cmc.malmo.domain.model.value.MemberId;
import makeus.cmc.malmo.domain.service.InviteCodeDomainService;
import makeus.cmc.malmo.domain.service.CoupleDomainService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CoupleService 단위 테스트")
class CoupleServiceTest {

    @Mock
    private InviteCodeDomainService inviteCodeDomainService;

    @Mock
    private CoupleDomainService coupleDomainService;

    @Mock
    private SaveCouplePort saveCouplePort;

    @Mock
    private SendSseEventPort sendSseEventPort;

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
            Long partnerId = 2L;
            LocalDate startLoveDate = LocalDate.of(2024, 1, 1);

            CoupleLinkUseCase.CoupleLinkCommand command = CoupleLinkUseCase.CoupleLinkCommand.builder()
                    .userId(userId)
                    .coupleCode(inviteCode)
                    .build();

            InviteCodeValue inviteCodeValue = InviteCodeValue.of(inviteCode);
            Member partner = mock(Member.class);
            Couple createdCouple = mock(Couple.class);
            Couple savedCouple = mock(Couple.class);
            
            given(partner.getId()).willReturn(partnerId);
            given(partner.getStartLoveDate()).willReturn(startLoveDate);
            given(savedCouple.getId()).willReturn(coupleId);

            given(inviteCodeDomainService.getMemberByInviteCode(inviteCodeValue)).willReturn(partner);
            given(coupleDomainService.createCoupleByInviteCode(MemberId.of(userId), MemberId.of(partnerId), startLoveDate))
                    .willReturn(createdCouple);
            given(saveCouplePort.saveCouple(createdCouple)).willReturn(savedCouple);

            // When
            CoupleLinkUseCase.CoupleLinkResponse response = coupleService.coupleLink(command);

            // Then
            assertThat(response).isNotNull();
            assertThat(response.getCoupleId()).isEqualTo(coupleId);

            then(sendSseEventPort).should().sendToMember(eq(MemberId.of(partnerId)), any(SendSseEventPort.NotificationEvent.class));
            then(inviteCodeDomainService).should().validateUsedInviteCode(inviteCodeValue);
            then(inviteCodeDomainService).should().getMemberByInviteCode(inviteCodeValue);
            then(coupleDomainService).should().createCoupleByInviteCode(MemberId.of(userId), MemberId.of(partnerId), startLoveDate);
            then(saveCouplePort).should().saveCouple(createdCouple);
        }

        @Test
        @DisplayName("실패: 존재하지 않는 커플 코드로 커플 연결 시 InviteCodeNotFoundException이 발생한다")
        void givenNonExistentCoupleCode_whenCoupleLink_thenThrowInviteCodeNotFoundException() {
            // Given
            Long userId = 1L;
            String invalidInviteCode = "INVALID123";

            CoupleLinkUseCase.CoupleLinkCommand command = CoupleLinkUseCase.CoupleLinkCommand.builder()
                    .userId(userId)
                    .coupleCode(invalidInviteCode)
                    .build();

            InviteCodeValue inviteCodeValue = InviteCodeValue.of(invalidInviteCode);

            given(inviteCodeDomainService.getMemberByInviteCode(inviteCodeValue))
                    .willThrow(new InviteCodeNotFoundException());

            // When & Then
            assertThatThrownBy(() -> coupleService.coupleLink(command))
                    .isInstanceOf(InviteCodeNotFoundException.class);

            then(inviteCodeDomainService).should().validateUsedInviteCode(inviteCodeValue);
            then(inviteCodeDomainService).should().getMemberByInviteCode(inviteCodeValue);
            then(coupleDomainService).should(never()).createCoupleByInviteCode(any(), any(), any());
            then(saveCouplePort).should(never()).saveCouple(any());
        }
    }
}
