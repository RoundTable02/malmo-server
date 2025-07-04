package makeus.cmc.malmo.service;

import makeus.cmc.malmo.adaptor.out.persistence.exception.MemberNotFoundException;
import makeus.cmc.malmo.application.port.in.GetMemberUseCase;
import makeus.cmc.malmo.application.port.in.GetPartnerUseCase;
import makeus.cmc.malmo.application.port.out.LoadMemberPort;
import makeus.cmc.malmo.application.port.out.LoadPartnerPort;
import makeus.cmc.malmo.application.service.MemberInfoService;
import makeus.cmc.malmo.domain.model.LoveType;
import makeus.cmc.malmo.domain.model.member.Member;
import makeus.cmc.malmo.domain.model.member.MemberState;
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
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("MemberInfoService 단위 테스트")
class MemberInfoServiceTest {

    @Mock
    private LoadMemberPort loadMemberPort;

    @Mock
    private LoadPartnerPort loadPartnerPort;

    @InjectMocks
    private MemberInfoService memberInfoService;

    @Nested
    @DisplayName("멤버 정보 조회 기능")
    class GetMemberInfoFeature {

        @Test
        @DisplayName("성공: 유효한 사용자 ID로 멤버 정보 조회가 성공한다")
        void givenValidUserId_whenGetMemberInfo_thenReturnMemberResponse() {
            // Given
            Long userId = 1L;
            String nickname = "테스트닉네임";
            String email = "test@example.com";
            MemberState memberState = MemberState.ALIVE;
            String loveTypeTitle = "안정형";
            float avoidanceRate = 0.3f;
            float anxietyRate = 0.2f;

            GetMemberUseCase.MemberInfoCommand command = GetMemberUseCase.MemberInfoCommand.builder()
                    .userId(userId)
                    .build();

            Member member = mock(Member.class);
            given(member.getNickname()).willReturn(nickname);
            given(member.getEmail()).willReturn(email);
            given(member.getMemberState()).willReturn(memberState);
            given(member.getAvoidanceRate()).willReturn(avoidanceRate);
            given(member.getAnxietyRate()).willReturn(anxietyRate);

            LoveType loveType = mock(LoveType.class);
            given(loveType.getTitle()).willReturn(loveTypeTitle);
            given(member.getLoveType()).willReturn(loveType);

            given(loadMemberPort.loadMemberById(userId)).willReturn(Optional.of(member));

            // When
            GetMemberUseCase.MemberResponseDto response = memberInfoService.getMemberInfo(command);

            // Then
            assertThat(response).isNotNull();
            assertThat(response.getNickname()).isEqualTo(nickname);
            assertThat(response.getEmail()).isEqualTo(email);
            assertThat(response.getMemberState()).isEqualTo(memberState);
            assertThat(response.getLoveTypeTitle()).isEqualTo(loveTypeTitle);
            assertThat(response.getAvoidanceRate()).isEqualTo(avoidanceRate);
            assertThat(response.getAnxietyRate()).isEqualTo(anxietyRate);

            then(loadMemberPort).should().loadMemberById(userId);
        }

        @Test
        @DisplayName("성공: 애착유형이 null인 멤버 정보 조회가 성공한다")
        void givenMemberWithNullLoveType_whenGetMemberInfo_thenReturnMemberResponseWithNullLoveType() {
            // Given
            Long userId = 1L;
            String nickname = "테스트닉네임";
            String email = "test@example.com";
            MemberState memberState = MemberState.ALIVE;
            float avoidanceRate = 0.3f;
            float anxietyRate = 0.2f;

            GetMemberUseCase.MemberInfoCommand command = GetMemberUseCase.MemberInfoCommand.builder()
                    .userId(userId)
                    .build();

            Member member = mock(Member.class);
            given(member.getNickname()).willReturn(nickname);
            given(member.getEmail()).willReturn(email);
            given(member.getMemberState()).willReturn(memberState);
            given(member.getAvoidanceRate()).willReturn(avoidanceRate);
            given(member.getAnxietyRate()).willReturn(anxietyRate);
            given(member.getLoveType()).willReturn(null);

            given(loadMemberPort.loadMemberById(userId)).willReturn(Optional.of(member));

            // When
            GetMemberUseCase.MemberResponseDto response = memberInfoService.getMemberInfo(command);

            // Then
            assertThat(response).isNotNull();
            assertThat(response.getNickname()).isEqualTo(nickname);
            assertThat(response.getEmail()).isEqualTo(email);
            assertThat(response.getMemberState()).isEqualTo(memberState);
            assertThat(response.getLoveTypeTitle()).isNull();
            assertThat(response.getAvoidanceRate()).isEqualTo(avoidanceRate);
            assertThat(response.getAnxietyRate()).isEqualTo(anxietyRate);

            then(loadMemberPort).should().loadMemberById(userId);
        }

        @Test
        @DisplayName("실패: 존재하지 않는 사용자 ID로 멤버 정보 조회 시 MemberNotFoundException이 발생한다")
        void givenNonExistentUserId_whenGetMemberInfo_thenThrowMemberNotFoundException() {
            // Given
            Long userId = 999L;

            GetMemberUseCase.MemberInfoCommand command = GetMemberUseCase.MemberInfoCommand.builder()
                    .userId(userId)
                    .build();

            given(loadMemberPort.loadMemberById(userId)).willReturn(Optional.empty());

            // When & Then
            assertThatThrownBy(() -> memberInfoService.getMemberInfo(command))
                    .isInstanceOf(MemberNotFoundException.class);

            then(loadMemberPort).should().loadMemberById(userId);
        }
    }

    @Nested
    @DisplayName("파트너 정보 조회 기능")
    class GetPartnerInfoFeature {

        @Test
        @DisplayName("성공: 유효한 사용자 ID로 파트너 정보 조회가 성공한다")
        void givenValidUserId_whenGetPartnerInfo_thenReturnPartnerMemberResponse() {
            // Given
            Long userId = 1L;
            String nickname = "파트너닉네임";
            String memberState = "ALIVE";
            String loveTypeTitle = "안정형";
            float avoidanceRate = 0.3f;
            float anxietyRate = 0.2f;
            LocalDate loveStartDate = LocalDate.of(2024, 1, 1);

            GetPartnerUseCase.PartnerInfoCommand command = GetPartnerUseCase.PartnerInfoCommand.builder()
                    .userId(userId)
                    .build();

            LoadPartnerPort.PartnerMemberRepositoryDto partner = mock(LoadPartnerPort.PartnerMemberRepositoryDto.class);
            given(partner.getNickname()).willReturn(nickname);
            given(partner.getMemberState()).willReturn(memberState);
            given(partner.getLoveTypeTitle()).willReturn(loveTypeTitle);
            given(partner.getAvoidanceRate()).willReturn(avoidanceRate);
            given(partner.getAnxietyRate()).willReturn(anxietyRate);
            given(partner.getLoveStartDate()).willReturn(loveStartDate);

            given(loadPartnerPort.loadPartnerByMemberId(userId)).willReturn(Optional.of(partner));

            // When
            GetPartnerUseCase.PartnerMemberResponseDto response = memberInfoService.getMemberInfo(command);

            // Then
            assertThat(response).isNotNull();
            assertThat(response.getNickname()).isEqualTo(nickname);
            assertThat(response.getMemberState()).isEqualTo(MemberState.ALIVE);
            assertThat(response.getLoveTypeTitle()).isEqualTo(loveTypeTitle);
            assertThat(response.getAvoidanceRate()).isEqualTo(avoidanceRate);
            assertThat(response.getAnxietyRate()).isEqualTo(anxietyRate);
            assertThat(response.getLoveStartDate()).isEqualTo(loveStartDate);

            then(loadPartnerPort).should().loadPartnerByMemberId(userId);
        }

        @Test
        @DisplayName("실패: 존재하지 않는 사용자 ID로 파트너 정보 조회 시 MemberNotFoundException이 발생한다")
        void givenNonExistentUserId_whenGetPartnerInfo_thenThrowMemberNotFoundException() {
            // Given
            Long userId = 999L;

            GetPartnerUseCase.PartnerInfoCommand command = GetPartnerUseCase.PartnerInfoCommand.builder()
                    .userId(userId)
                    .build();

            given(loadPartnerPort.loadPartnerByMemberId(userId)).willReturn(Optional.empty());

            // When & Then
            assertThatThrownBy(() -> memberInfoService.getMemberInfo(command))
                    .isInstanceOf(MemberNotFoundException.class);

            then(loadPartnerPort).should().loadPartnerByMemberId(userId);
        }
    }
}