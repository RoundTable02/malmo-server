package makeus.cmc.malmo.domain_service;

import makeus.cmc.malmo.application.port.out.LoadLoveTypePort;
import makeus.cmc.malmo.application.port.out.LoadLoveTypeQuestionsPort;
import makeus.cmc.malmo.domain.exception.LoveTypeNotFoundException;
import makeus.cmc.malmo.domain.exception.LoveTypeQuestionNotFoundException;
import makeus.cmc.malmo.domain.model.love_type.LoveType;
import makeus.cmc.malmo.domain.model.love_type.LoveTypeCategory;
import makeus.cmc.malmo.domain.model.love_type.LoveTypeQuestion;
import makeus.cmc.malmo.domain.model.value.LoveTypeId;
import makeus.cmc.malmo.domain.service.LoveTypeDomainService;
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
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("LoveTypeDomainService 단위 테스트")
class LoveTypeDomainServiceTest {

    @Mock
    private LoadLoveTypePort loadLoveTypePort;

    @Mock
    private LoadLoveTypeQuestionsPort loadLoveTypeQuestionsPort;

    @InjectMocks
    private LoveTypeDomainService loveTypeDomainService;

    @Nested
    @DisplayName("애착 유형 ID로 조회 기능")
    class GetLoveTypeByIdFeature {

        @Test
        @DisplayName("성공: 유효한 애착 유형 ID로 애착 유형을 조회한다")
        void givenValidLoveTypeId_whenGetLoveTypeById_thenReturnLoveType() {
            // Given
            LoveTypeId loveTypeId = LoveTypeId.of(1L);
            LoveType loveType = mock(LoveType.class);

            given(loadLoveTypePort.findLoveTypeById(1L)).willReturn(Optional.of(loveType));

            // When
            LoveType result = loveTypeDomainService.getLoveTypeById(loveTypeId);

            // Then
            assertThat(result).isEqualTo(loveType);
            then(loadLoveTypePort).should().findLoveTypeById(1L);
        }

        @Test
        @DisplayName("실패: 존재하지 않는 애착 유형 ID로 조회 시 LoveTypeNotFoundException이 발생한다")
        void givenNonExistentLoveTypeId_whenGetLoveTypeById_thenThrowLoveTypeNotFoundException() {
            // Given
            LoveTypeId loveTypeId = LoveTypeId.of(999L);

            given(loadLoveTypePort.findLoveTypeById(999L)).willReturn(Optional.empty());

            // When & Then
            assertThatThrownBy(() -> loveTypeDomainService.getLoveTypeById(loveTypeId))
                    .isInstanceOf(LoveTypeNotFoundException.class);

            then(loadLoveTypePort).should().findLoveTypeById(999L);
        }

        @Test
        @DisplayName("경계값: 최소 ID 값으로 애착 유형을 조회한다")
        void givenMinLoveTypeId_whenGetLoveTypeById_thenReturnLoveType() {
            // Given
            LoveTypeId loveTypeId = LoveTypeId.of(1L);
            LoveType loveType = mock(LoveType.class);

            given(loadLoveTypePort.findLoveTypeById(1L)).willReturn(Optional.of(loveType));

            // When
            LoveType result = loveTypeDomainService.getLoveTypeById(loveTypeId);

            // Then
            assertThat(result).isEqualTo(loveType);
            then(loadLoveTypePort).should().findLoveTypeById(1L);
        }

        @Test
        @DisplayName("경계값: 최대 ID 값으로 애착 유형을 조회한다")
        void givenMaxLoveTypeId_whenGetLoveTypeById_thenReturnLoveType() {
            // Given
            LoveTypeId loveTypeId = LoveTypeId.of(Long.MAX_VALUE);
            LoveType loveType = mock(LoveType.class);

            given(loadLoveTypePort.findLoveTypeById(Long.MAX_VALUE)).willReturn(Optional.of(loveType));

            // When
            LoveType result = loveTypeDomainService.getLoveTypeById(loveTypeId);

            // Then
            assertThat(result).isEqualTo(loveType);
            then(loadLoveTypePort).should().findLoveTypeById(Long.MAX_VALUE);
        }
    }

    @Nested
    @DisplayName("애착 유형 계산 기능")
    class CalculateLoveTypeFeature {

        @Test
        @DisplayName("성공: 정상적인 테스트 결과로 애착 유형을 계산한다")
        void givenValidTestResults_whenCalculateLoveType_thenReturnCalculationResult() {
            // Given
            List<LoveTypeDomainService.TestResultInput> results = List.of(
                    new LoveTypeDomainService.TestResultInput(1L, 5),
                    new LoveTypeDomainService.TestResultInput(2L, 3),
                    new LoveTypeDomainService.TestResultInput(3L, 4)
            );

            LoveTypeQuestion anxietyQuestion = createLoveTypeQuestion(1L, 1, "불안형 질문", true);
            LoveTypeQuestion avoidanceQuestion1 = createLoveTypeQuestion(2L, 2, "회피형 질문1", false);
            LoveTypeQuestion avoidanceQuestion2 = createLoveTypeQuestion(3L, 3, "회피형 질문2", false);

            List<LoveTypeQuestion> questions = List.of(anxietyQuestion, avoidanceQuestion1, avoidanceQuestion2);
            LoveType expectedLoveType = mock(LoveType.class);

            given(loadLoveTypeQuestionsPort.loadLoveTypeQuestions()).willReturn(questions);
            given(anxietyQuestion.getScore(5)).willReturn(3);
            given(avoidanceQuestion1.getScore(3)).willReturn(2);
            given(avoidanceQuestion2.getScore(4)).willReturn(3);

            try (MockedStatic<LoveType> loveTypeMock = mockStatic(LoveType.class)) {
                loveTypeMock.when(() -> LoveType.findLoveTypeCategory(5.0f, 3.0f))
                        .thenReturn(LoveTypeCategory.STABLE_TYPE);
                given(loadLoveTypePort.findLoveTypeByLoveTypeCategory(LoveTypeCategory.STABLE_TYPE))
                        .willReturn(Optional.of(expectedLoveType));

                // When
                LoveTypeDomainService.LoveTypeCalculationResult result = loveTypeDomainService.calculateLoveType(results);

                // Then
                assertThat(result.loveType()).isEqualTo(expectedLoveType);
                assertThat(result.avoidanceScore()).isEqualTo(5.0f);
                assertThat(result.anxietyScore()).isEqualTo(3.0f);

                then(loadLoveTypeQuestionsPort).should().loadLoveTypeQuestions();
                then(anxietyQuestion).should().getScore(5);
                then(avoidanceQuestion1).should().getScore(3);
                then(avoidanceQuestion2).should().getScore(4);
                loveTypeMock.verify(() -> LoveType.findLoveTypeCategory(5.0f, 3.0f));
                then(loadLoveTypePort).should().findLoveTypeByLoveTypeCategory(LoveTypeCategory.STABLE_TYPE);
            }
        }

        @Test
        @DisplayName("성공: 불안형만 있는 테스트 결과로 애착 유형을 계산한다")
        void givenOnlyAnxietyResults_whenCalculateLoveType_thenReturnCalculationResult() {
            // Given
            List<LoveTypeDomainService.TestResultInput> results = List.of(
                    new LoveTypeDomainService.TestResultInput(1L, 5),
                    new LoveTypeDomainService.TestResultInput(2L, 4)
            );

            LoveTypeQuestion anxietyQuestion1 = createLoveTypeQuestion(1L, 1, "불안형 질문1", true);
            LoveTypeQuestion anxietyQuestion2 = createLoveTypeQuestion(2L, 2, "불안형 질문2", true);

            List<LoveTypeQuestion> questions = List.of(anxietyQuestion1, anxietyQuestion2);
            LoveType expectedLoveType = mock(LoveType.class);

            given(loadLoveTypeQuestionsPort.loadLoveTypeQuestions()).willReturn(questions);
            given(anxietyQuestion1.getScore(5)).willReturn(3);
            given(anxietyQuestion2.getScore(4)).willReturn(2);

            try (MockedStatic<LoveType> loveTypeMock = mockStatic(LoveType.class)) {
                loveTypeMock.when(() -> LoveType.findLoveTypeCategory(0.0f, 5.0f))
                        .thenReturn(LoveTypeCategory.ANXIETY_TYPE);
                given(loadLoveTypePort.findLoveTypeByLoveTypeCategory(LoveTypeCategory.ANXIETY_TYPE))
                        .willReturn(Optional.of(expectedLoveType));

                // When
                LoveTypeDomainService.LoveTypeCalculationResult result = loveTypeDomainService.calculateLoveType(results);

                // Then
                assertThat(result.loveType()).isEqualTo(expectedLoveType);
                assertThat(result.avoidanceScore()).isEqualTo(0.0f);
                assertThat(result.anxietyScore()).isEqualTo(5.0f);

                then(loadLoveTypeQuestionsPort).should().loadLoveTypeQuestions();
                then(anxietyQuestion1).should().getScore(5);
                then(anxietyQuestion2).should().getScore(4);
                loveTypeMock.verify(() -> LoveType.findLoveTypeCategory(0.0f, 5.0f));
                then(loadLoveTypePort).should().findLoveTypeByLoveTypeCategory(LoveTypeCategory.ANXIETY_TYPE);
            }
        }

        @Test
        @DisplayName("성공: 회피형만 있는 테스트 결과로 애착 유형을 계산한다")
        void givenOnlyAvoidanceResults_whenCalculateLoveType_thenReturnCalculationResult() {
            // Given
            List<LoveTypeDomainService.TestResultInput> results = List.of(
                    new LoveTypeDomainService.TestResultInput(1L, 3),
                    new LoveTypeDomainService.TestResultInput(2L, 5)
            );

            LoveTypeQuestion avoidanceQuestion1 = createLoveTypeQuestion(1L, 1, "회피형 질문1", false);
            LoveTypeQuestion avoidanceQuestion2 = createLoveTypeQuestion(2L, 2, "회피형 질문2", false);

            List<LoveTypeQuestion> questions = List.of(avoidanceQuestion1, avoidanceQuestion2);
            LoveType expectedLoveType = mock(LoveType.class);

            given(loadLoveTypeQuestionsPort.loadLoveTypeQuestions()).willReturn(questions);
            given(avoidanceQuestion1.getScore(3)).willReturn(2);
            given(avoidanceQuestion2.getScore(5)).willReturn(3);

            try (MockedStatic<LoveType> loveTypeMock = mockStatic(LoveType.class)) {
                loveTypeMock.when(() -> LoveType.findLoveTypeCategory(5.0f, 0.0f))
                        .thenReturn(LoveTypeCategory.AVOIDANCE_TYPE);
                given(loadLoveTypePort.findLoveTypeByLoveTypeCategory(LoveTypeCategory.AVOIDANCE_TYPE))
                        .willReturn(Optional.of(expectedLoveType));

                // When
                LoveTypeDomainService.LoveTypeCalculationResult result = loveTypeDomainService.calculateLoveType(results);

                // Then
                assertThat(result.loveType()).isEqualTo(expectedLoveType);
                assertThat(result.avoidanceScore()).isEqualTo(5.0f);
                assertThat(result.anxietyScore()).isEqualTo(0.0f);

                then(loadLoveTypeQuestionsPort).should().loadLoveTypeQuestions();
                then(avoidanceQuestion1).should().getScore(3);
                then(avoidanceQuestion2).should().getScore(5);
                loveTypeMock.verify(() -> LoveType.findLoveTypeCategory(5.0f, 0.0f));
                then(loadLoveTypePort).should().findLoveTypeByLoveTypeCategory(LoveTypeCategory.AVOIDANCE_TYPE);
            }
        }

        @Test
        @DisplayName("실패: 존재하지 않는 질문 ID가 포함된 경우 LoveTypeQuestionNotFoundException이 발생한다")
        void givenNonExistentQuestionId_whenCalculateLoveType_thenThrowLoveTypeQuestionNotFoundException() {
            // Given
            List<LoveTypeDomainService.TestResultInput> results = List.of(
                    new LoveTypeDomainService.TestResultInput(1L, 5),
                    new LoveTypeDomainService.TestResultInput(999L, 3)
            );

            LoveTypeQuestion question = createLoveTypeQuestion(1L, 1, "질문", true);
            List<LoveTypeQuestion> questions = List.of(question);

            given(loadLoveTypeQuestionsPort.loadLoveTypeQuestions()).willReturn(questions);

            // When & Then
            assertThatThrownBy(() -> loveTypeDomainService.calculateLoveType(results))
                    .isInstanceOf(LoveTypeQuestionNotFoundException.class);

            then(loadLoveTypeQuestionsPort).should().loadLoveTypeQuestions();
        }

        @Test
        @DisplayName("실패: 계산된 카테고리에 해당하는 애착 유형이 없을 때 LoveTypeNotFoundException이 발생한다")
        void givenNoLoveTypeForCategory_whenCalculateLoveType_thenThrowLoveTypeNotFoundException() {
            // Given
            List<LoveTypeDomainService.TestResultInput> results = List.of(
                    new LoveTypeDomainService.TestResultInput(1L, 5)
            );

            LoveTypeQuestion question = createLoveTypeQuestion(1L, 1, "질문", true);
            List<LoveTypeQuestion> questions = List.of(question);

            given(loadLoveTypeQuestionsPort.loadLoveTypeQuestions()).willReturn(questions);
            given(question.getScore(5)).willReturn(3);

            try (MockedStatic<LoveType> loveTypeMock = mockStatic(LoveType.class)) {
                loveTypeMock.when(() -> LoveType.findLoveTypeCategory(0.0f, 3.0f))
                        .thenReturn(LoveTypeCategory.ANXIETY_TYPE);
                given(loadLoveTypePort.findLoveTypeByLoveTypeCategory(LoveTypeCategory.ANXIETY_TYPE))
                        .willReturn(Optional.empty());

                // When & Then
                assertThatThrownBy(() -> loveTypeDomainService.calculateLoveType(results))
                        .isInstanceOf(LoveTypeNotFoundException.class);

                then(loadLoveTypeQuestionsPort).should().loadLoveTypeQuestions();
                then(question).should().getScore(5);
                loveTypeMock.verify(() -> LoveType.findLoveTypeCategory(0.0f, 3.0f));
                then(loadLoveTypePort).should().findLoveTypeByLoveTypeCategory(LoveTypeCategory.ANXIETY_TYPE);
            }
        }

        @Test
        @DisplayName("경계값: 빈 테스트 결과로 애착 유형을 계산한다")
        void givenEmptyResults_whenCalculateLoveType_thenReturnZeroScores() {
            // Given
            List<LoveTypeDomainService.TestResultInput> results = Collections.emptyList();
            List<LoveTypeQuestion> questions = Collections.emptyList();
            LoveType expectedLoveType = mock(LoveType.class);

            given(loadLoveTypeQuestionsPort.loadLoveTypeQuestions()).willReturn(questions);

            try (MockedStatic<LoveType> loveTypeMock = mockStatic(LoveType.class)) {
                loveTypeMock.when(() -> LoveType.findLoveTypeCategory(0.0f, 0.0f))
                        .thenReturn(LoveTypeCategory.STABLE_TYPE);
                given(loadLoveTypePort.findLoveTypeByLoveTypeCategory(LoveTypeCategory.STABLE_TYPE))
                        .willReturn(Optional.of(expectedLoveType));

                // When
                LoveTypeDomainService.LoveTypeCalculationResult result = loveTypeDomainService.calculateLoveType(results);

                // Then
                assertThat(result.loveType()).isEqualTo(expectedLoveType);
                assertThat(result.avoidanceScore()).isEqualTo(0.0f);
                assertThat(result.anxietyScore()).isEqualTo(0.0f);

                then(loadLoveTypeQuestionsPort).should().loadLoveTypeQuestions();
                loveTypeMock.verify(() -> LoveType.findLoveTypeCategory(0.0f, 0.0f));
                then(loadLoveTypePort).should().findLoveTypeByLoveTypeCategory(LoveTypeCategory.STABLE_TYPE);
            }
        }

        @Test
        @DisplayName("경계값: 최대 점수로 애착 유형을 계산한다")
        void givenMaxScores_whenCalculateLoveType_thenReturnCalculationResult() {
            // Given
            List<LoveTypeDomainService.TestResultInput> results = List.of(
                    new LoveTypeDomainService.TestResultInput(1L, 7),
                    new LoveTypeDomainService.TestResultInput(2L, 7)
            );

            LoveTypeQuestion anxietyQuestion = createLoveTypeQuestion(1L, 1, "불안형 질문", true);
            LoveTypeQuestion avoidanceQuestion = createLoveTypeQuestion(2L, 2, "회피형 질문", false);

            List<LoveTypeQuestion> questions = List.of(anxietyQuestion, avoidanceQuestion);
            LoveType expectedLoveType = mock(LoveType.class);

            given(loadLoveTypeQuestionsPort.loadLoveTypeQuestions()).willReturn(questions);
            given(anxietyQuestion.getScore(7)).willReturn(7);
            given(avoidanceQuestion.getScore(7)).willReturn(7);

            try (MockedStatic<LoveType> loveTypeMock = mockStatic(LoveType.class)) {
                loveTypeMock.when(() -> LoveType.findLoveTypeCategory(7.0f, 7.0f))
                        .thenReturn(LoveTypeCategory.CONFUSION_TYPE);
                given(loadLoveTypePort.findLoveTypeByLoveTypeCategory(LoveTypeCategory.CONFUSION_TYPE))
                        .willReturn(Optional.of(expectedLoveType));

                // When
                LoveTypeDomainService.LoveTypeCalculationResult result = loveTypeDomainService.calculateLoveType(results);

                // Then
                assertThat(result.loveType()).isEqualTo(expectedLoveType);
                assertThat(result.avoidanceScore()).isEqualTo(7.0f);
                assertThat(result.anxietyScore()).isEqualTo(7.0f);

                then(loadLoveTypeQuestionsPort).should().loadLoveTypeQuestions();
                then(anxietyQuestion).should().getScore(7);
                then(avoidanceQuestion).should().getScore(7);
                loveTypeMock.verify(() -> LoveType.findLoveTypeCategory(7.0f, 7.0f));
                then(loadLoveTypePort).should().findLoveTypeByLoveTypeCategory(LoveTypeCategory.CONFUSION_TYPE);
            }
        }

        @Test
        @DisplayName("경계값: 최소 점수로 애착 유형을 계산한다")
        void givenMinScores_whenCalculateLoveType_thenReturnCalculationResult() {
            // Given
            List<LoveTypeDomainService.TestResultInput> results = List.of(
                    new LoveTypeDomainService.TestResultInput(1L, 1),
                    new LoveTypeDomainService.TestResultInput(2L, 1)
            );

            LoveTypeQuestion anxietyQuestion = createLoveTypeQuestion(1L, 1, "불안형 질문", true);
            LoveTypeQuestion avoidanceQuestion = createLoveTypeQuestion(2L, 2, "회피형 질문", false);

            List<LoveTypeQuestion> questions = List.of(anxietyQuestion, avoidanceQuestion);
            LoveType expectedLoveType = mock(LoveType.class);

            given(loadLoveTypeQuestionsPort.loadLoveTypeQuestions()).willReturn(questions);
            given(anxietyQuestion.getScore(1)).willReturn(1);
            given(avoidanceQuestion.getScore(1)).willReturn(1);

            try (MockedStatic<LoveType> loveTypeMock = mockStatic(LoveType.class)) {
                loveTypeMock.when(() -> LoveType.findLoveTypeCategory(1.0f, 1.0f))
                        .thenReturn(LoveTypeCategory.STABLE_TYPE);
                given(loadLoveTypePort.findLoveTypeByLoveTypeCategory(LoveTypeCategory.STABLE_TYPE))
                        .willReturn(Optional.of(expectedLoveType));

                // When
                LoveTypeDomainService.LoveTypeCalculationResult result = loveTypeDomainService.calculateLoveType(results);

                // Then
                assertThat(result.loveType()).isEqualTo(expectedLoveType);
                assertThat(result.avoidanceScore()).isEqualTo(1.0f);
                assertThat(result.anxietyScore()).isEqualTo(1.0f);

                then(loadLoveTypeQuestionsPort).should().loadLoveTypeQuestions();
                then(anxietyQuestion).should().getScore(1);
                then(avoidanceQuestion).should().getScore(1);
                loveTypeMock.verify(() -> LoveType.findLoveTypeCategory(1.0f, 1.0f));
                then(loadLoveTypePort).should().findLoveTypeByLoveTypeCategory(LoveTypeCategory.STABLE_TYPE);
            }
        }
    }

    private LoveTypeQuestion createLoveTypeQuestion(Long id, int questionNumber, String content, boolean isAnxietyType) {
        LoveTypeQuestion question = mock(LoveTypeQuestion.class);
        given(question.getId()).willReturn(id);
        given(question.isAnxietyType()).willReturn(isAnxietyType);
        return question;
    }
}