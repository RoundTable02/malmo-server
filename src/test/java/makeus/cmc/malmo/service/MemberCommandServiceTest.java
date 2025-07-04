package makeus.cmc.malmo.service;

import makeus.cmc.malmo.adaptor.out.persistence.exception.MemberNotFoundException;
import makeus.cmc.malmo.application.port.in.UpdateMemberUseCase;
import makeus.cmc.malmo.application.port.out.LoadMemberPort;
import makeus.cmc.malmo.application.port.out.SaveMemberPort;
import makeus.cmc.malmo.application.service.MemberCommandService;
import makeus.cmc.malmo.domain.model.member.Member;
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
@DisplayName("MemberCommandService 단위 테스트")
class MemberCommandServiceTest {

    @Mock
    private LoadMemberPort loadMemberPort;

    @Mock
    private SaveMemberPort saveMemberPort;

    @InjectMocks
    private MemberCommandService memberCommandService;

    @Nested
    @DisplayName("멤버 정보 수정 기능")
    class UpdateMemberFeature {

        @Test
        @DisplayName("성공: 유효한 멤버 정보로 멤버 정보 수정이 성공한다")
        void givenValidMemberInfo_whenUpdateMember_thenReturnUpdateMemberResponse() {
            // Given
            Long memberId = 1L;
            String nickname = "수정된닉네임";
            String email = "updated@example.com";

            UpdateMemberUseCase.UpdateMemberCommand command = UpdateMemberUseCase.UpdateMemberCommand.builder()
                    .memberId(memberId)
                    .nickname(nickname)
                    .email(email)
                    .build();

            Member member = mock(Member.class);
            Member savedMember = mock(Member.class);
            given(savedMember.getNickname()).willReturn(nickname);
            given(savedMember.getEmail()).willReturn(email);

            given(loadMemberPort.loadMemberById(memberId)).willReturn(Optional.of(member));
            given(saveMemberPort.saveMember(member)).willReturn(savedMember);

            // When
            UpdateMemberUseCase.UpdateMemberResponseDto response = memberCommandService.updateMember(command);

            // Then
            assertThat(response).isNotNull();
            assertThat(response.getNickname()).isEqualTo(nickname);
            assertThat(response.getEmail()).isEqualTo(email);

            then(loadMemberPort).should().loadMemberById(memberId);
            then(member).should().updateMemberProfile(nickname, email);
            then(saveMemberPort).should().saveMember(member);
        }

        @Test
        @DisplayName("실패: 존재하지 않는 멤버 ID로 멤버 정보 수정 시 MemberNotFoundException이 발생한다")
        void givenNonExistentMemberId_whenUpdateMember_thenThrowMemberNotFoundException() {
            // Given
            Long memberId = 999L;
            String nickname = "수정된닉네임";
            String email = "updated@example.com";

            UpdateMemberUseCase.UpdateMemberCommand command = UpdateMemberUseCase.UpdateMemberCommand.builder()
                    .memberId(memberId)
                    .nickname(nickname)
                    .email(email)
                    .build();

            given(loadMemberPort.loadMemberById(memberId)).willReturn(Optional.empty());

            // When & Then
            assertThatThrownBy(() -> memberCommandService.updateMember(command))
                    .isInstanceOf(MemberNotFoundException.class);

            then(loadMemberPort).should().loadMemberById(memberId);
            then(saveMemberPort).should(never()).saveMember(any());
        }
    }
}