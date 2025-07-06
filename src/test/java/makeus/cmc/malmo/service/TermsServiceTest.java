package makeus.cmc.malmo.service;

import makeus.cmc.malmo.application.port.in.TermsUseCase;
import makeus.cmc.malmo.application.port.out.LoadTermsPort;
import makeus.cmc.malmo.application.service.TermsService;
import makeus.cmc.malmo.domain.model.terms.Terms;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("TermsService 단위 테스트")
class TermsServiceTest {

    @Mock
    private LoadTermsPort loadTermsPort;

    @InjectMocks
    private TermsService termsService;

    @Nested
    @DisplayName("약관 조회 기능")
    class GetTermsFeature {

        @Test
        @DisplayName("성공: 최신 약관 목록을 조회한다")
        void givenLatestTermsExist_whenGetTerms_thenReturnTermsResponseList() {
            // Given
            Terms terms1 = mock(Terms.class);
            given(terms1.getId()).willReturn(1L);
            given(terms1.getTitle()).willReturn("서비스 이용약관");
            given(terms1.getContent()).willReturn("서비스 이용약관 내용입니다.");
            given(terms1.getVersion()).willReturn(1.0f);
            given(terms1.isRequired()).willReturn(true);

            Terms terms2 = mock(Terms.class);
            given(terms2.getId()).willReturn(2L);
            given(terms2.getTitle()).willReturn("개인정보 처리방침");
            given(terms2.getContent()).willReturn("개인정보 처리방침 내용입니다.");
            given(terms2.getVersion()).willReturn(1.1f);
            given(terms2.isRequired()).willReturn(true);

            Terms terms3 = mock(Terms.class);
            given(terms3.getId()).willReturn(3L);
            given(terms3.getTitle()).willReturn("마케팅 정보 수신동의");
            given(terms3.getContent()).willReturn("마케팅 정보 수신동의 내용입니다.");
            given(terms3.getVersion()).willReturn(1.0f);
            given(terms3.isRequired()).willReturn(false);

            List<Terms> termsList = Arrays.asList(terms1, terms2, terms3);

            given(loadTermsPort.loadLatestTerms()).willReturn(termsList);

            // When
            List<TermsUseCase.TermsDto> response = termsService.getTerms().getTermsList();

            // Then
            assertThat(response).isNotNull();
            assertThat(response).hasSize(3);

            TermsUseCase.TermsDto response1 = response.get(0);
            assertThat(response1.getTermsId()).isEqualTo(1L);
            assertThat(response1.getTitle()).isEqualTo("서비스 이용약관");
            assertThat(response1.getContent()).isEqualTo("서비스 이용약관 내용입니다.");
            assertThat(response1.getVersion()).isEqualTo(1.0f);
            assertThat(response1.isRequired()).isTrue();

            TermsUseCase.TermsDto response2 = response.get(1);
            assertThat(response2.getTermsId()).isEqualTo(2L);
            assertThat(response2.getTitle()).isEqualTo("개인정보 처리방침");
            assertThat(response2.getContent()).isEqualTo("개인정보 처리방침 내용입니다.");
            assertThat(response2.getVersion()).isEqualTo(1.1f);
            assertThat(response2.isRequired()).isTrue();

            TermsUseCase.TermsDto response3 = response.get(2);
            assertThat(response3.getTermsId()).isEqualTo(3L);
            assertThat(response3.getTitle()).isEqualTo("마케팅 정보 수신동의");
            assertThat(response3.getContent()).isEqualTo("마케팅 정보 수신동의 내용입니다.");
            assertThat(response3.getVersion()).isEqualTo(1.0f);
            assertThat(response3.isRequired()).isFalse();

            then(loadTermsPort).should().loadLatestTerms();
        }

        @Test
        @DisplayName("성공: 약관이 없을 때 빈 리스트를 반환한다")
        void givenNoTermsExist_whenGetTerms_thenReturnEmptyList() {
            // Given
            given(loadTermsPort.loadLatestTerms()).willReturn(Collections.emptyList());

            // When
            List<TermsUseCase.TermsDto> response = termsService.getTerms().getTermsList();

            // Then
            assertThat(response).isNotNull();
            assertThat(response).isEmpty();

            then(loadTermsPort).should().loadLatestTerms();
        }

        @Test
        @DisplayName("성공: 단일 약관만 존재할 때 정상적으로 조회한다")
        void givenSingleTermsExists_whenGetTerms_thenReturnSingleTermsResponse() {
            // Given
            Terms terms = mock(Terms.class);
            given(terms.getId()).willReturn(1L);
            given(terms.getTitle()).willReturn("서비스 이용약관");
            given(terms.getContent()).willReturn("서비스 이용약관 내용입니다.");
            given(terms.getVersion()).willReturn(1.0f);
            given(terms.isRequired()).willReturn(true);

            List<Terms> termsList = Arrays.asList(terms);

            given(loadTermsPort.loadLatestTerms()).willReturn(termsList);

            // When
            List<TermsUseCase.TermsDto> response = termsService.getTerms().getTermsList();

            // Then
            assertThat(response).isNotNull();
            assertThat(response).hasSize(1);

            TermsUseCase.TermsDto termsDto = response.get(0);
            assertThat(termsDto.getTermsId()).isEqualTo(1L);
            assertThat(termsDto.getTitle()).isEqualTo("서비스 이용약관");
            assertThat(termsDto.getContent()).isEqualTo("서비스 이용약관 내용입니다.");
            assertThat(termsDto.getVersion()).isEqualTo(1.0f);
            assertThat(termsDto.isRequired()).isTrue();

            then(loadTermsPort).should().loadLatestTerms();
        }

        @Test
        @DisplayName("성공: 필수 약관과 선택 약관이 모두 있을 때 정상적으로 조회한다")
        void givenRequiredAndOptionalTermsExist_whenGetTerms_thenReturnAllTermsResponse() {
            // Given
            Terms requiredTerms = mock(Terms.class);
            given(requiredTerms.getId()).willReturn(1L);
            given(requiredTerms.getTitle()).willReturn("필수 약관");
            given(requiredTerms.getContent()).willReturn("필수 약관 내용입니다.");
            given(requiredTerms.getVersion()).willReturn(1.0f);
            given(requiredTerms.isRequired()).willReturn(true);

            Terms optionalTerms = mock(Terms.class);
            given(optionalTerms.getId()).willReturn(2L);
            given(optionalTerms.getTitle()).willReturn("선택 약관");
            given(optionalTerms.getContent()).willReturn("선택 약관 내용입니다.");
            given(optionalTerms.getVersion()).willReturn(1.0f);
            given(optionalTerms.isRequired()).willReturn(false);

            List<Terms> termsList = Arrays.asList(requiredTerms, optionalTerms);

            given(loadTermsPort.loadLatestTerms()).willReturn(termsList);

            // When
            List<TermsUseCase.TermsDto> response = termsService.getTerms().getTermsList();

            // Then
            assertThat(response).isNotNull();
            assertThat(response).hasSize(2);

            // 필수 약관 검증
            TermsUseCase.TermsDto requiredResponse = response.stream()
                    .filter(TermsUseCase.TermsDto::isRequired)
                    .findFirst()
                    .orElseThrow();
            assertThat(requiredResponse.getTermsId()).isEqualTo(1L);
            assertThat(requiredResponse.getTitle()).isEqualTo("필수 약관");
            assertThat(requiredResponse.isRequired()).isTrue();

            // 선택 약관 검증
            TermsUseCase.TermsDto optionalResponse = response.stream()
                    .filter(termsDto -> !termsDto.isRequired())
                    .findFirst()
                    .orElseThrow();
            assertThat(optionalResponse.getTermsId()).isEqualTo(2L);
            assertThat(optionalResponse.getTitle()).isEqualTo("선택 약관");
            assertThat(optionalResponse.isRequired()).isFalse();

            then(loadTermsPort).should().loadLatestTerms();
        }
    }
}