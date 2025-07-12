package makeus.cmc.malmo.domain_service;

import makeus.cmc.malmo.application.port.out.LoadTermsAgreementPort;
import makeus.cmc.malmo.application.port.out.LoadTermsPort;
import makeus.cmc.malmo.application.port.out.SaveMemberTermsAgreement;
import makeus.cmc.malmo.domain.exception.TermsNotFoundException;
import makeus.cmc.malmo.domain.model.terms.MemberTermsAgreement;
import makeus.cmc.malmo.domain.model.terms.Terms;
import makeus.cmc.malmo.domain.value.id.MemberId;
import makeus.cmc.malmo.domain.value.id.TermsId;
import makeus.cmc.malmo.domain.service.TermsAgreementDomainService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("TermsAgreementDomainService 단위 테스트")
class TermsAgreementDomainServiceTest {

    @Mock
    private LoadTermsPort loadTermsPort;

    @Mock
    private LoadTermsAgreementPort loadTermsAgreementPort;

    @Mock
    private SaveMemberTermsAgreement saveMemberTermsAgreement;

    @InjectMocks
    private TermsAgreementDomainService termsAgreementDomainService;

    @Nested
    @DisplayName("약관 동의 처리 기능")
    class ProcessAgreementsFeature {

        @Test
        @DisplayName("성공: 단일 약관 동의를 처리한다")
        void givenSingleAgreement_whenProcessAgreements_thenSaveAgreement() {
            // Given
            MemberId memberId = MemberId.of(1L);
            List<TermsAgreementDomainService.TermAgreementInput> agreementInputs = List.of(
                    new TermsAgreementDomainService.TermAgreementInput(1L, true)
            );

            Terms terms = mock(Terms.class);
            MemberTermsAgreement memberTermsAgreement = mock(MemberTermsAgreement.class);

            given(loadTermsPort.loadTermsById(1L)).willReturn(Optional.of(terms));
            given(terms.getId()).willReturn(1L);

            try (MockedStatic<MemberTermsAgreement> memberTermsAgreementMock = mockStatic(MemberTermsAgreement.class)) {
                memberTermsAgreementMock.when(() -> MemberTermsAgreement.signTerms(
                        memberId,
                        TermsId.of(1L),
                        true
                )).thenReturn(memberTermsAgreement);

                // When
                termsAgreementDomainService.processAgreements(memberId, agreementInputs);

                // Then
                then(loadTermsPort).should().loadTermsById(1L);
                then(terms).should().getId();
                memberTermsAgreementMock.verify(() -> MemberTermsAgreement.signTerms(
                        memberId,
                        TermsId.of(1L),
                        true
                ));
                then(saveMemberTermsAgreement).should().saveMemberTermsAgreement(memberTermsAgreement);
            }
        }

        @Test
        @DisplayName("실패: 존재하지 않는 약관 ID로 처리 시 TermsNotFoundException이 발생한다")
        void givenNonExistentTermsId_whenProcessAgreements_thenThrowTermsNotFoundException() {
            // Given
            MemberId memberId = MemberId.of(1L);
            List<TermsAgreementDomainService.TermAgreementInput> agreementInputs = List.of(
                    new TermsAgreementDomainService.TermAgreementInput(999L, true)
            );

            given(loadTermsPort.loadTermsById(999L)).willReturn(Optional.empty());

            // When & Then
            assertThatThrownBy(() -> termsAgreementDomainService.processAgreements(memberId, agreementInputs))
                    .isInstanceOf(TermsNotFoundException.class);

            then(loadTermsPort).should().loadTermsById(999L);
            then(saveMemberTermsAgreement).should(never()).saveMemberTermsAgreement(any());
        }

        @Test
        @DisplayName("경계값: 빈 약관 동의 목록을 처리한다")
        void givenEmptyAgreements_whenProcessAgreements_thenNoSaveOperation() {
            // Given
            MemberId memberId = MemberId.of(1L);
            List<TermsAgreementDomainService.TermAgreementInput> agreementInputs = Collections.emptyList();

            // When
            termsAgreementDomainService.processAgreements(memberId, agreementInputs);

            // Then
            then(loadTermsPort).should(never()).loadTermsById(any());
            then(saveMemberTermsAgreement).should(never()).saveMemberTermsAgreement(any());
        }
    }

    @Nested
    @DisplayName("약관 동의 조회 기능")
    class GetTermsAgreementFeature {

        @Test
        @DisplayName("성공: 유효한 멤버 ID와 약관 ID로 약관 동의를 조회한다")
        void givenValidMemberIdAndTermsId_whenGetTermsAgreement_thenReturnAgreement() {
            // Given
            MemberId memberId = MemberId.of(1L);
            TermsId termsId = TermsId.of(1L);
            MemberTermsAgreement expectedAgreement = mock(MemberTermsAgreement.class);

            given(loadTermsAgreementPort.loadTermsAgreementByMemberIdAndTermsId(memberId, termsId))
                    .willReturn(Optional.of(expectedAgreement));

            // When
            MemberTermsAgreement result = termsAgreementDomainService.getTermsAgreement(memberId, termsId);

            // Then
            assertThat(result).isEqualTo(expectedAgreement);
            then(loadTermsAgreementPort).should().loadTermsAgreementByMemberIdAndTermsId(memberId, termsId);
        }

        @Test
        @DisplayName("실패: 존재하지 않는 약관 동의 조회 시 TermsNotFoundException이 발생한다")
        void givenNonExistentAgreement_whenGetTermsAgreement_thenThrowTermsNotFoundException() {
            // Given
            MemberId memberId = MemberId.of(999L);
            TermsId termsId = TermsId.of(999L);

            given(loadTermsAgreementPort.loadTermsAgreementByMemberIdAndTermsId(memberId, termsId))
                    .willReturn(Optional.empty());

            // When & Then
            assertThatThrownBy(() -> termsAgreementDomainService.getTermsAgreement(memberId, termsId))
                    .isInstanceOf(TermsNotFoundException.class);

            then(loadTermsAgreementPort).should().loadTermsAgreementByMemberIdAndTermsId(memberId, termsId);
        }
    }
}