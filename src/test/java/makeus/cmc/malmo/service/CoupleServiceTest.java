package makeus.cmc.malmo.service;

import makeus.cmc.malmo.domain.exception.CoupleCodeNotFoundException;
import makeus.cmc.malmo.domain.exception.MemberNotFoundException;
import makeus.cmc.malmo.application.port.in.CoupleLinkUseCase;
import makeus.cmc.malmo.application.port.out.SaveCouplePort;
import makeus.cmc.malmo.application.service.CoupleService;
import makeus.cmc.malmo.domain.model.couple.Couple;
import makeus.cmc.malmo.domain.model.member.CoupleCode;
import makeus.cmc.malmo.domain.model.member.Member;
import makeus.cmc.malmo.domain.model.value.MemberId;
import makeus.cmc.malmo.domain.service.CoupleCodeDomainService;
import makeus.cmc.malmo.domain.service.CoupleDomainService;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CoupleService 단위 테스트")
class CoupleServiceTest {

    @Mock
    private MemberDomainService memberDomainService;

    @Mock
    private CoupleCodeDomainService coupleCodeDomainService;

    @Mock
    private CoupleDomainService coupleDomainService;

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

            CoupleLinkUseCase.CoupleLinkCommand command = CoupleLinkUseCase.CoupleLinkCommand.builder()
                    .userId(userId)
                    .coupleCode(inviteCode)
                    .build();

            Member member = mock(Member.class);
            CoupleCode coupleCode = mock(CoupleCode.class);
            Couple createdCouple = mock(Couple.class);
            Couple savedCouple = mock(Couple.class);
            given(savedCouple.getId()).willReturn(coupleId);

            given(memberDomainService.getMemberById(MemberId.of(userId))).willReturn(member);
            given(coupleCodeDomainService.getCoupleCodeByInviteCode(inviteCode)).willReturn(coupleCode);
            given(coupleDomainService.createCoupleByCoupleCode(member, coupleCode)).willReturn(createdCouple);
            given(saveCouplePort.saveCouple(createdCouple)).willReturn(savedCouple);

            // When
            CoupleLinkUseCase.CoupleLinkResponse response = coupleService.coupleLink(command);

            // Then
            assertThat(response).isNotNull();
            assertThat(response.getCoupleId()).isEqualTo(coupleId);

            then(memberDomainService).should().getMemberById(MemberId.of(userId));
            then(coupleCodeDomainService).should().getCoupleCodeByInviteCode(inviteCode);
            then(coupleDomainService).should().createCoupleByCoupleCode(member, coupleCode);
            then(saveCouplePort).should().saveCouple(createdCouple);
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

            given(memberDomainService.getMemberById(MemberId.of(userId)))
                    .willThrow(new MemberNotFoundException());

            // When & Then
            assertThatThrownBy(() -> coupleService.coupleLink(command))
                    .isInstanceOf(MemberNotFoundException.class);

            then(memberDomainService).should().getMemberById(MemberId.of(userId));
            then(coupleCodeDomainService).should(never()).getCoupleCodeByInviteCode(any());
            then(coupleDomainService).should(never()).createCoupleByCoupleCode(any(), any());
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

            given(memberDomainService.getMemberById(MemberId.of(userId))).willReturn(member);
            given(coupleCodeDomainService.getCoupleCodeByInviteCode(invalidInviteCode))
                    .willThrow(new CoupleCodeNotFoundException());

            // When & Then
            assertThatThrownBy(() -> coupleService.coupleLink(command))
                    .isInstanceOf(CoupleCodeNotFoundException.class);

            then(memberDomainService).should().getMemberById(MemberId.of(userId));
            then(coupleCodeDomainService).should().getCoupleCodeByInviteCode(invalidInviteCode);
            then(coupleDomainService).should(never()).createCoupleByCoupleCode(any(), any());
            then(saveCouplePort).should(never()).saveCouple(any());
        }
    }
}
