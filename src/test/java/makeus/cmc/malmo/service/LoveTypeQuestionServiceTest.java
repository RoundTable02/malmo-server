package makeus.cmc.malmo.service;

import makeus.cmc.malmo.application.port.in.GetLoveTypeQuestionsUseCase;
import makeus.cmc.malmo.application.port.out.LoadLoveTypeQuestionsPort;
import makeus.cmc.malmo.application.service.LoveTypeQuestionService;
import makeus.cmc.malmo.domain.model.love_type.LoveTypeQuestion;
import makeus.cmc.malmo.domain.value.type.LoveTypeQuestionType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
@DisplayName("LoveTypeQuestionService 단위 테스트")
class LoveTypeQuestionServiceTest {

    @Mock
    private LoadLoveTypeQuestionsPort loadLoveTypeQuestionsPort;

    @InjectMocks
    private LoveTypeQuestionService loveTypeQuestionService;

    @Nested
    @DisplayName("사랑 유형 질문 목록 조회 기능")
    class GetLoveTypeQuestionsFeature {

        @Test
        @DisplayName("성공: 사랑 유형 질문 목록 조회가 성공한다")
        void givenLoveTypeQuestions_whenGetLoveTypeQuestions_thenReturnLoveTypeQuestionsResponse() {
            // Given
            List<LoveTypeQuestion> loveTypeQuestions = createLoveTypeQuestions();
            given(loadLoveTypeQuestionsPort.loadLoveTypeQuestions()).willReturn(loveTypeQuestions);

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

            then(loadLoveTypeQuestionsPort).should().loadLoveTypeQuestions();
        }

        @Test
        @DisplayName("성공: 빈 질문 목록이 반환되어도 정상 처리된다")
        void givenEmptyLoveTypeQuestions_whenGetLoveTypeQuestions_thenReturnEmptyResponse() {
            // Given
            List<LoveTypeQuestion> emptyQuestions = new ArrayList<>();
            given(loadLoveTypeQuestionsPort.loadLoveTypeQuestions()).willReturn(emptyQuestions);

            // When
            GetLoveTypeQuestionsUseCase.LoveTypeQuestionsResponseDto response = loveTypeQuestionService.getLoveTypeQuestions();

            // Then
            assertThat(response).isNotNull();
            assertThat(response.getList()).isEmpty();

            then(loadLoveTypeQuestionsPort).should().loadLoveTypeQuestions();
        }
    }

    private List<LoveTypeQuestion> createLoveTypeQuestions() {
        return List.of(
                LoveTypeQuestion.builder()
                        .id(1L)
                        .questionNumber(1)
                        .content("나는 상대방과 가까워지는 것이 쉽다.")
                        .isReversed(false)
                        .loveTypeQuestionType(LoveTypeQuestionType.AVOIDANCE)
                        .weight(1)
                        .build(),
                LoveTypeQuestion.builder()
                        .id(2L)
                        .questionNumber(2)
                        .content("나는 상대방에게 의존하는 것이 편안하다.")
                        .isReversed(false)
                        .loveTypeQuestionType(LoveTypeQuestionType.AVOIDANCE)
                        .weight(1)
                        .build(),
                LoveTypeQuestion.builder()
                        .id(3L)
                        .questionNumber(3)
                        .content("나는 상대방이 나를 떠날까봐 걱정한다.")
                        .isReversed(false)
                        .loveTypeQuestionType(LoveTypeQuestionType.ANXIETY)
                        .weight(1)
                        .build()
        );
    }
}