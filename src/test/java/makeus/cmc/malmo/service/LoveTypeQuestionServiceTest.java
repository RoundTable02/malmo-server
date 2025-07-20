package makeus.cmc.malmo.service;

import makeus.cmc.malmo.application.port.in.GetLoveTypeQuestionsUseCase;
import makeus.cmc.malmo.application.port.out.LoadLoveTypeQuestionDataPort;
import makeus.cmc.malmo.application.service.LoveTypeQuestionService;
import makeus.cmc.malmo.domain.model.love_type.LoveTypeQuestionData;
import makeus.cmc.malmo.domain.value.type.LoveTypeQuestionType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
@DisplayName("LoveTypeQuestionService 단위 테스트")
class LoveTypeQuestionServiceTest {

    @Mock
    private LoadLoveTypeQuestionDataPort loadLoveTypeQuestionDataPort;

    @InjectMocks
    private LoveTypeQuestionService loveTypeQuestionService;

    @Nested
    @DisplayName("애착 유형 질문 목록 조회 기능")
    class GetLoveTypeQuestionsFeature {

        @Test
        @DisplayName("성공: 애착 유형 질문 목록 조회가 성공한다")
        void givenLoveTypeQuestions_whenGetLoveTypeQuestions_thenReturnLoveTypeQuestionsResponse() {
            // Given
            Map<Long, LoveTypeQuestionData> loveTypeQuestionsMap = createLoveTypeQuestionsMap();
            given(loadLoveTypeQuestionDataPort.loadLoveTypeData()).willReturn(loveTypeQuestionsMap);

            // When
            GetLoveTypeQuestionsUseCase.LoveTypeQuestionsResponseDto response = loveTypeQuestionService.getLoveTypeQuestions();

            // Then
            assertThat(response).isNotNull();
            assertThat(response.getList()).hasSize(3);
            
            GetLoveTypeQuestionsUseCase.LoveTypeQuestionDto firstQuestion = response.getList().get(0);
            assertThat(firstQuestion.getQuestionNumber()).isEqualTo(1);
            assertThat(firstQuestion.getContent()).isEqualTo("나는 상대방과 가까워지는 것이 쉽다.");
            
            GetLoveTypeQuestionsUseCase.LoveTypeQuestionDto secondQuestion = response.getList().get(1);
            assertThat(secondQuestion.getQuestionNumber()).isEqualTo(2);
            assertThat(secondQuestion.getContent()).isEqualTo("나는 상대방에게 의존하는 것이 편안하다.");
            
            GetLoveTypeQuestionsUseCase.LoveTypeQuestionDto thirdQuestion = response.getList().get(2);
            assertThat(thirdQuestion.getQuestionNumber()).isEqualTo(3);
            assertThat(thirdQuestion.getContent()).isEqualTo("나는 상대방이 나를 떠날까봐 걱정한다.");

            then(loadLoveTypeQuestionDataPort).should().loadLoveTypeData();
        }

        @Test
        @DisplayName("성공: 빈 질문 목록이 반환되어도 정상 처리된다")
        void givenEmptyLoveTypeQuestions_whenGetLoveTypeQuestions_thenReturnEmptyResponse() {
            // Given
            Map<Long, LoveTypeQuestionData> emptyMap = new HashMap<>();
            given(loadLoveTypeQuestionDataPort.loadLoveTypeData()).willReturn(emptyMap);

            // When
            GetLoveTypeQuestionsUseCase.LoveTypeQuestionsResponseDto response = loveTypeQuestionService.getLoveTypeQuestions();

            // Then
            assertThat(response).isNotNull();
            assertThat(response.getList()).isEmpty();

            then(loadLoveTypeQuestionDataPort).should().loadLoveTypeData();
        }
    }

    private Map<Long, LoveTypeQuestionData> createLoveTypeQuestionsMap() {
        Map<Long, LoveTypeQuestionData> map = new HashMap<>();

        // Note: LoveTypeQuestionData는 immutable하므로 직접 생성할 수 없음
        // 실제 테스트에서는 Mock 객체를 사용하거나 JSON 파싱을 통해 생성
        LoveTypeQuestionData question1 = createMockQuestion(1L, 1, "나는 상대방과 가까워지는 것이 쉽다.", false, LoveTypeQuestionType.AVOIDANCE, 1);
        LoveTypeQuestionData question2 = createMockQuestion(2L, 2, "나는 상대방에게 의존하는 것이 편안하다.", false, LoveTypeQuestionType.AVOIDANCE, 1);
        LoveTypeQuestionData question3 = createMockQuestion(3L, 3, "나는 상대방이 나를 떠날까봐 걱정한다.", false, LoveTypeQuestionType.ANXIETY, 1);

        map.put(1L, question1);
        map.put(2L, question2);
        map.put(3L, question3);

        return map;
    }

    private LoveTypeQuestionData createMockQuestion(Long id, int questionNumber, String content, boolean isReversed, LoveTypeQuestionType type, int weight) {
        // LoveTypeQuestionData의 필드들을 설정하는 방법
        // 실제로는 JSON 데이터를 통해 생성되므로 여기서는 리플렉션을 사용하거나 Mock을 사용
        try {
            LoveTypeQuestionData question = new LoveTypeQuestionData();

            // 리플렉션을 통해 private 필드에 값 설정
            java.lang.reflect.Field idField = question.getClass().getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(question, id);

            java.lang.reflect.Field questionNumberField = question.getClass().getDeclaredField("questionNumber");
            questionNumberField.setAccessible(true);
            questionNumberField.set(question, questionNumber);

            java.lang.reflect.Field contentField = question.getClass().getDeclaredField("content");
            contentField.setAccessible(true);
            contentField.set(question, content);

            java.lang.reflect.Field isReversedField = question.getClass().getDeclaredField("isReversed");
            isReversedField.setAccessible(true);
            isReversedField.set(question, isReversed);

            java.lang.reflect.Field typeField = question.getClass().getDeclaredField("loveTypeQuestionType");
            typeField.setAccessible(true);
            typeField.set(question, type);

            java.lang.reflect.Field weightField = question.getClass().getDeclaredField("weight");
            weightField.setAccessible(true);
            weightField.set(question, weight);

            return question;
        } catch (Exception e) {
            throw new RuntimeException("Failed to create mock question", e);
        }
    }
}