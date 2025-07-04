package makeus.cmc.malmo.service;

import makeus.cmc.malmo.adaptor.out.persistence.exception.TermsNotFoundException;
import makeus.cmc.malmo.application.port.in.UpdateTermsAgreementUseCase;
import makeus.cmc.malmo.application.port.out.LoadTermsAgreementPort;
import makeus.cmc.malmo.application.port.out.SaveMemberTermsAgreement;
import makeus.cmc.malmo.application.service.TermsAgreementService;
import makeus.cmc.malmo.domain.model.terms.MemberTermsAgreement;
import makeus.cmc.malmo.domain.model.value.MemberId;
import makeus.cmc.malmo.domain.model.value.TermsId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("TermsAgreementService 단위 테스트")
class TermsAgreementServiceTest {

    @Mock
    private LoadTermsAgreementPort termsAgreementPort;

    @Mock
    private SaveMemberTermsAgreement saveMemberTermsAgreement;

    @InjectMocks
    private TermsAgreementService termsAgreementService;

    @Nested
    @DisplayName("약관 동의 수정 기능")
    class UpdateTermsAgreementFeature {

        @Test
        @DisplayName("성공: 유효한 약관 동의 정보로 약관 동의 수정이 성공한다")
        void givenValidTermsAgreementInfo_whenUpdateTermsAgreement_thenReturnTermsAgreementResponse() {
            // Given
            Long memberId = 1L;
            Long termsId1 = 1L;
            Long termsId2 = 2L;
            Boolean isAgreed1 = true;
            Boolean isAgreed2 = false;

            UpdateTermsAgreementUseCase.TermsDto termsDto1 = UpdateTermsAgreementUseCase.TermsDto.builder()
                    .termsId(termsId1)
                    .isAgreed(isAgreed1)
                    .build();

            UpdateTermsAgreementUseCase.TermsDto termsDto2 = UpdateTermsAgreementUseCase.TermsDto.builder()
                    .termsId(termsId2)
                    .isAgreed(isAgreed2)
                    .build();

            List<UpdateTermsAgreementUseCase.TermsDto> termsList = Arrays.asList(termsDto1, termsDto2);

            UpdateTermsAgreementUseCase.TermsAgreementCommand command = UpdateTermsAgreementUseCase.TermsAgreementCommand.builder()
                    .memberId(memberId)
                    .terms(termsList)
                    .build();

            MemberTermsAgreement memberTermsAgreement1 = mock(MemberTermsAgreement.class);
            given(memberTermsAgreement1.isAgreed()).willReturn(isAgreed1);

            MemberTermsAgreement memberTermsAgreement2 = mock(MemberTermsAgreement.class);
            given(memberTermsAgreement2.isAgreed()).willReturn(isAgreed2);

            given(termsAgreementPort.loadTermsAgreementByMemberIdAndTermsId(MemberId.of(memberId), TermsId.of(termsId1)))
                    .willReturn(Optional.of(memberTermsAgreement1));
            given(termsAgreementPort.loadTermsAgreementByMemberIdAndTermsId(MemberId.of(memberId), TermsId.of(termsId2)))
                    .willReturn(Optional.of(memberTermsAgreement2));

            // When
            UpdateTermsAgreementUseCase.TermsAgreementResponse response = termsAgreementService.updateTermsAgreement(command);

            // Then
            assertThat(response).isNotNull();
            assertThat(response.getTerms()).hasSize(2);

            UpdateTermsAgreementUseCase.TermsDto responseTerms1 = response.getTerms().get(0);
            assertThat(responseTerms1.getTermsId()).isEqualTo(termsId1);
            assertThat(responseTerms1.getIsAgreed()).isEqualTo(isAgreed1);

            UpdateTermsAgreementUseCase.TermsDto responseTerms2 = response.getTerms().get(1);
            assertThat(responseTerms2.getTermsId()).isEqualTo(termsId2);
            assertThat(responseTerms2.getIsAgreed()).isEqualTo(isAgreed2);

            then(termsAgreementPort).should().loadTermsAgreementByMemberIdAndTermsId(MemberId.of(memberId), TermsId.of(termsId1));
            then(termsAgreementPort).should().loadTermsAgreementByMemberIdAndTermsId(MemberId.of(memberId), TermsId.of(termsId2));
            then(memberTermsAgreement1).should().updateAgreement(isAgreed1);
            then(memberTermsAgreement2).should().updateAgreement(isAgreed2);
            then(saveMemberTermsAgreement).should().saveMemberTermsAgreement(memberTermsAgreement1);
            then(saveMemberTermsAgreement).should().saveMemberTermsAgreement(memberTermsAgreement2);
        }

        @Test
        @DisplayName("성공: 단일 약관 동의 정보로 약관 동의 수정이 성공한다")
        void givenSingleTermsAgreementInfo_whenUpdateTermsAgreement_thenReturnTermsAgreementResponse() {
            // Given
            Long memberId = 1L;
            Long termsId = 1L;
            Boolean isAgreed = true;

            UpdateTermsAgreementUseCase.TermsDto termsDto = UpdateTermsAgreementUseCase.TermsDto.builder()
                    .termsId(termsId)
                    .isAgreed(isAgreed)
                    .build();

            List<UpdateTermsAgreementUseCase.TermsDto> termsList = Arrays.asList(termsDto);

            UpdateTermsAgreementUseCase.TermsAgreementCommand command = UpdateTermsAgreementUseCase.TermsAgreementCommand.builder()
                    .memberId(memberId)
                    .terms(termsList)
                    .build();

            MemberTermsAgreement memberTermsAgreement = mock(MemberTermsAgreement.class);
            given(memberTermsAgreement.isAgreed()).willReturn(isAgreed);

            given(termsAgreementPort.loadTermsAgreementByMemberIdAndTermsId(MemberId.of(memberId), TermsId.of(termsId)))
                    .willReturn(Optional.of(memberTermsAgreement));

            // When
            UpdateTermsAgreementUseCase.TermsAgreementResponse response = termsAgreementService.updateTermsAgreement(command);

            // Then
            assertThat(response).isNotNull();
            assertThat(response.getTerms()).hasSize(1);

            UpdateTermsAgreementUseCase.TermsDto responseTerms = response.getTerms().get(0);
            assertThat(responseTerms.getTermsId()).isEqualTo(termsId);
            assertThat(responseTerms.getIsAgreed()).isEqualTo(isAgreed);

            then(termsAgreementPort).should().loadTermsAgreementByMemberIdAndTermsId(MemberId.of(memberId), TermsId.of(termsId));
            then(memberTermsAgreement).should().updateAgreement(isAgreed);
            then(saveMemberTermsAgreement).should().saveMemberTermsAgreement(memberTermsAgreement);
        }

        @Test
        @DisplayName("실패: 존재하지 않는 약관 동의 정보로 약관 동의 수정 시 TermsNotFoundException이 발생한다")
        void givenNonExistentTermsAgreement_whenUpdateTermsAgreement_thenThrowTermsNotFoundException() {
            // Given
            Long memberId = 1L;
            Long termsId = 999L;
            Boolean isAgreed = true;

            UpdateTermsAgreementUseCase.TermsDto termsDto = UpdateTermsAgreementUseCase.TermsDto.builder()
                    .termsId(termsId)
                    .isAgreed(isAgreed)
                    .build();

            List<UpdateTermsAgreementUseCase.TermsDto> termsList = Arrays.asList(termsDto);

            UpdateTermsAgreementUseCase.TermsAgreementCommand command = UpdateTermsAgreementUseCase.TermsAgreementCommand.builder()
                    .memberId(memberId)
                    .terms(termsList)
                    .build();

            given(termsAgreementPort.loadTermsAgreementByMemberIdAndTermsId(MemberId.of(memberId), TermsId.of(termsId)))
                    .willReturn(Optional.empty());

            // When & Then
            assertThatThrownBy(() -> termsAgreementService.updateTermsAgreement(command))
                    .isInstanceOf(TermsNotFoundException.class);

            then(termsAgreementPort).should().loadTermsAgreementByMemberIdAndTermsId(MemberId.of(memberId), TermsId.of(termsId));
            then(saveMemberTermsAgreement).should(never()).saveMemberTermsAgreement(any());
        }

        @Test
        @DisplayName("실패: 첫 번째 약관은 존재하지만 두 번째 약관이 존재하지 않을 때 TermsNotFoundException이 발생한다")
        void givenFirstTermsExistsButSecondTermsNotExists_whenUpdateTermsAgreement_thenThrowTermsNotFoundException() {
            // Given
            Long memberId = 1L;
            Long termsId1 = 1L;
            Long termsId2 = 999L;
            Boolean isAgreed1 = true;
            Boolean isAgreed2 = false;

            UpdateTermsAgreementUseCase.TermsDto termsDto1 = UpdateTermsAgreementUseCase.TermsDto.builder()
                    .termsId(termsId1)
                    .isAgreed(isAgreed1)
                    .build();

            UpdateTermsAgreementUseCase.TermsDto termsDto2 = UpdateTermsAgreementUseCase.TermsDto.builder()
                    .termsId(termsId2)
                    .isAgreed(isAgreed2)
                    .build();

            List<UpdateTermsAgreementUseCase.TermsDto> termsList = Arrays.asList(termsDto1, termsDto2);

            UpdateTermsAgreementUseCase.TermsAgreementCommand command = UpdateTermsAgreementUseCase.TermsAgreementCommand.builder()
                    .memberId(memberId)
                    .terms(termsList)
                    .build();

            MemberTermsAgreement memberTermsAgreement1 = mock(MemberTermsAgreement.class);

            given(termsAgreementPort.loadTermsAgreementByMemberIdAndTermsId(MemberId.of(memberId), TermsId.of(termsId1)))
                    .willReturn(Optional.of(memberTermsAgreement1));
            given(termsAgreementPort.loadTermsAgreementByMemberIdAndTermsId(MemberId.of(memberId), TermsId.of(termsId2)))
                    .willReturn(Optional.empty());

            // When & Then
            assertThatThrownBy(() -> termsAgreementService.updateTermsAgreement(command))
                    .isInstanceOf(TermsNotFoundException.class);

            then(termsAgreementPort).should().loadTermsAgreementByMemberIdAndTermsId(MemberId.of(memberId), TermsId.of(termsId1));
            then(termsAgreementPort).should().loadTermsAgreementByMemberIdAndTermsId(MemberId.of(memberId), TermsId.of(termsId2));
            then(memberTermsAgreement1).should().updateAgreement(isAgreed1);
            then(saveMemberTermsAgreement).should().saveMemberTermsAgreement(memberTermsAgreement1);
        }
    }
}