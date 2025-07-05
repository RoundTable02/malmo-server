package makeus.cmc.malmo.service;

import makeus.cmc.malmo.adaptor.out.persistence.exception.LoveTypeNotFoundException;
import makeus.cmc.malmo.adaptor.out.persistence.exception.LoveTypeQuestionNotFoundException;
import makeus.cmc.malmo.adaptor.out.persistence.exception.MemberNotFoundException;
import makeus.cmc.malmo.application.port.in.GetLoveTypeUseCase;
import makeus.cmc.malmo.application.port.in.UpdateMemberLoveTypeUseCase;
import makeus.cmc.malmo.application.port.out.LoadLoveTypePort;
import makeus.cmc.malmo.application.port.out.LoadLoveTypeQuestionsPort;
import makeus.cmc.malmo.application.port.out.LoadMemberPort;
import makeus.cmc.malmo.application.port.out.SaveMemberPort;
import makeus.cmc.malmo.application.service.LoveTypeService;
import makeus.cmc.malmo.domain.model.love_type.LoveType;
import makeus.cmc.malmo.domain.model.love_type.LoveTypeCategory;
import makeus.cmc.malmo.domain.model.love_type.LoveTypeQuestion;
import makeus.cmc.malmo.domain.model.love_type.LoveTypeQuestionType;
import makeus.cmc.malmo.domain.model.member.Member;
import makeus.cmc.malmo.domain.model.value.LoveTypeId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("LoveTypeService 단위 테스트")
class LoveTypeServiceTest {

    @Mock
    private LoadLoveTypePort loadLoveTypePort;

    @Mock
    private LoadLoveTypeQuestionsPort loadLoveTypeQuestionsPort;

    @Mock
    private LoadMemberPort loadMemberPort;

    @Mock
    private SaveMemberPort saveMemberPort;

    @InjectMocks
    private LoveTypeService loveTypeService;

    @Nested
    @DisplayName("사랑 유형 조회 기능")
    class GetLoveTypeFeature {

        @Test
        @DisplayName("성공: 유효한 사랑 유형 ID로 조회가 성공한다")
        void givenValidLoveTypeId_whenGetLoveType_thenReturnLoveTypeResponse() {
            // Given
            Long loveTypeId = 1L;
            GetLoveTypeUseCase.GetLoveTypeCommand command = GetLoveTypeUseCase.GetLoveTypeCommand.builder()
                    .loveTypeId(loveTypeId)
                    .build();

            LoveType loveType = createLoveType();
            given(loadLoveTypePort.findLoveTypeById(loveTypeId)).willReturn(Optional.of(loveType));

            // When
            GetLoveTypeUseCase.GetLoveTypeResponseDto response = loveTypeService.getLoveType(command);

            // Then
            assertThat(response).isNotNull();
            assertThat(response.getLoveTypeId()).isEqualTo(loveTypeId);
            assertThat(response.getTitle()).isEqualTo("안정형");
            assertThat(response.getSummary()).isEqualTo("안정적인 애착 유형");
            assertThat(response.getContent()).isEqualTo("안정적인 애착 스타일입니다.");
            assertThat(response.getImageUrl()).isEqualTo("http://example.com/secure.jpg");

            then(loadLoveTypePort).should().findLoveTypeById(loveTypeId);
        }

        @Test
        @DisplayName("실패: 존재하지 않는 사랑 유형 ID로 조회 시 LoveTypeNotFoundException이 발생한다")
        void givenNonExistentLoveTypeId_whenGetLoveType_thenThrowLoveTypeNotFoundException() {
            // Given
            Long nonExistentLoveTypeId = 999L;
            GetLoveTypeUseCase.GetLoveTypeCommand command = GetLoveTypeUseCase.GetLoveTypeCommand.builder()
                    .loveTypeId(nonExistentLoveTypeId)
                    .build();

            given(loadLoveTypePort.findLoveTypeById(nonExistentLoveTypeId)).willReturn(Optional.empty());

            // When & Then
            assertThatThrownBy(() -> loveTypeService.getLoveType(command))
                    .isInstanceOf(LoveTypeNotFoundException.class);

            then(loadLoveTypePort).should().findLoveTypeById(nonExistentLoveTypeId);
        }
    }

    @Nested
    @DisplayName("회원 사랑 유형 업데이트 기능")
    class UpdateMemberLoveTypeFeature {

        @Test
        @DisplayName("성공: 유효한 회원 ID와 테스트 결과로 사랑 유형 업데이트가 성공한다")
        void givenValidMemberAndTestResults_whenUpdateMemberLoveType_thenReturnRegisterLoveTypeResponse() {
            // Given
            Long memberId = 1L;
            List<UpdateMemberLoveTypeUseCase.LoveTypeTestResult> testResults = createLoveTypeTestResults();
            UpdateMemberLoveTypeUseCase.UpdateMemberLoveTypeCommand command = 
                    UpdateMemberLoveTypeUseCase.UpdateMemberLoveTypeCommand.builder()
                            .memberId(memberId)
                            .results(testResults)
                            .build();

            Member member = mock(Member.class);
            List<LoveTypeQuestion> questions = createLoveTypeQuestions();
            LoveType loveType = createLoveType();

            given(loadMemberPort.loadMemberById(memberId)).willReturn(Optional.of(member));
            given(loadLoveTypeQuestionsPort.loadLoveTypeQuestions()).willReturn(questions);
            given(loadLoveTypePort.findLoveTypeByLoveTypeCategory(LoveTypeCategory.STABLE_TYPE))
                    .willReturn(Optional.of(loveType));
            given(saveMemberPort.saveMember(member)).willReturn(member);

            // When
            UpdateMemberLoveTypeUseCase.RegisterLoveTypeResponseDto response = 
                    loveTypeService.updateMemberLoveType(command);

            // Then
            assertThat(response).isNotNull();
            assertThat(response.getLoveTypeId()).isEqualTo(1L);
            assertThat(response.getTitle()).isEqualTo("안정형");
            assertThat(response.getSummary()).isEqualTo("안정적인 애착 유형");
            assertThat(response.getContent()).isEqualTo("안정적인 애착 스타일입니다.");
            assertThat(response.getImageUrl()).isEqualTo("http://example.com/secure.jpg");

            then(loadMemberPort).should().loadMemberById(memberId);
            then(loadLoveTypeQuestionsPort).should().loadLoveTypeQuestions();
            then(loadLoveTypePort).should().findLoveTypeByLoveTypeCategory(LoveTypeCategory.STABLE_TYPE);
            then(member).should().updateLoveTypeId(any(LoveTypeId.class));
            then(saveMemberPort).should().saveMember(member);
        }

        @Test
        @DisplayName("실패: 존재하지 않는 회원 ID로 업데이트 시 MemberNotFoundException이 발생한다")
        void givenNonExistentMemberId_whenUpdateMemberLoveType_thenThrowMemberNotFoundException() {
            // Given
            Long nonExistentMemberId = 999L;
            List<UpdateMemberLoveTypeUseCase.LoveTypeTestResult> testResults = createLoveTypeTestResults();
            UpdateMemberLoveTypeUseCase.UpdateMemberLoveTypeCommand command = 
                    UpdateMemberLoveTypeUseCase.UpdateMemberLoveTypeCommand.builder()
                            .memberId(nonExistentMemberId)
                            .results(testResults)
                            .build();

            given(loadMemberPort.loadMemberById(nonExistentMemberId)).willReturn(Optional.empty());

            // When & Then
            assertThatThrownBy(() -> loveTypeService.updateMemberLoveType(command))
                    .isInstanceOf(MemberNotFoundException.class);

            then(loadMemberPort).should().loadMemberById(nonExistentMemberId);
            then(loadLoveTypeQuestionsPort).should(never()).loadLoveTypeQuestions();
            then(loadLoveTypePort).should(never()).findLoveTypeByLoveTypeCategory(any());
            then(saveMemberPort).should(never()).saveMember(any());
        }

        @Test
        @DisplayName("실패: 존재하지 않는 질문 ID가 포함된 경우 LoveTypeQuestionNotFoundException이 발생한다")
        void givenNonExistentQuestionId_whenUpdateMemberLoveType_thenThrowLoveTypeQuestionNotFoundException() {
            // Given
            Long memberId = 1L;
            List<UpdateMemberLoveTypeUseCase.LoveTypeTestResult> testResults = createInvalidLoveTypeTestResults();
            UpdateMemberLoveTypeUseCase.UpdateMemberLoveTypeCommand command = 
                    UpdateMemberLoveTypeUseCase.UpdateMemberLoveTypeCommand.builder()
                            .memberId(memberId)
                            .results(testResults)
                            .build();

            Member member = mock(Member.class);
            List<LoveTypeQuestion> questions = createLoveTypeQuestions();

            given(loadMemberPort.loadMemberById(memberId)).willReturn(Optional.of(member));
            given(loadLoveTypeQuestionsPort.loadLoveTypeQuestions()).willReturn(questions);

            // When & Then
            assertThatThrownBy(() -> loveTypeService.updateMemberLoveType(command))
                    .isInstanceOf(LoveTypeQuestionNotFoundException.class);

            then(loadMemberPort).should().loadMemberById(memberId);
            then(loadLoveTypeQuestionsPort).should().loadLoveTypeQuestions();
            then(loadLoveTypePort).should(never()).findLoveTypeByLoveTypeCategory(any());
            then(saveMemberPort).should(never()).saveMember(any());
        }

        @Test
        @DisplayName("실패: 계산된 사랑 유형 카테고리에 해당하는 LoveType이 없을 때 LoveTypeNotFoundException이 발생한다")
        void givenCalculatedLoveTypeCategoryNotFound_whenUpdateMemberLoveType_thenThrowLoveTypeNotFoundException() {
            // Given
            Long memberId = 1L;
            List<UpdateMemberLoveTypeUseCase.LoveTypeTestResult> testResults = createLoveTypeTestResults();
            UpdateMemberLoveTypeUseCase.UpdateMemberLoveTypeCommand command = 
                    UpdateMemberLoveTypeUseCase.UpdateMemberLoveTypeCommand.builder()
                            .memberId(memberId)
                            .results(testResults)
                            .build();

            Member member = mock(Member.class);
            List<LoveTypeQuestion> questions = createLoveTypeQuestions();

            given(loadMemberPort.loadMemberById(memberId)).willReturn(Optional.of(member));
            given(loadLoveTypeQuestionsPort.loadLoveTypeQuestions()).willReturn(questions);
            given(loadLoveTypePort.findLoveTypeByLoveTypeCategory(LoveTypeCategory.STABLE_TYPE))
                    .willReturn(Optional.empty());

            // When & Then
            assertThatThrownBy(() -> loveTypeService.updateMemberLoveType(command))
                    .isInstanceOf(LoveTypeNotFoundException.class);

            then(loadMemberPort).should().loadMemberById(memberId);
            then(loadLoveTypeQuestionsPort).should().loadLoveTypeQuestions();
            then(loadLoveTypePort).should().findLoveTypeByLoveTypeCategory(LoveTypeCategory.STABLE_TYPE);
            then(saveMemberPort).should(never()).saveMember(any());
        }
    }

    private LoveType createLoveType() {
        return LoveType.builder()
                .id(1L)
                .title("안정형")
                .summary("안정적인 애착 유형")
                .content("안정적인 애착 스타일입니다.")
                .imageUrl("http://example.com/secure.jpg")
                .loveTypeCategory(LoveTypeCategory.STABLE_TYPE)
                .build();
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

    private List<UpdateMemberLoveTypeUseCase.LoveTypeTestResult> createLoveTypeTestResults() {
        return List.of(
                UpdateMemberLoveTypeUseCase.LoveTypeTestResult.builder()
                        .questionId(1L)
                        .score(1)
                        .build(),
                UpdateMemberLoveTypeUseCase.LoveTypeTestResult.builder()
                        .questionId(2L)
                        .score(2)
                        .build(),
                UpdateMemberLoveTypeUseCase.LoveTypeTestResult.builder()
                        .questionId(3L)
                        .score(1)
                        .build()
        );
    }

    private List<UpdateMemberLoveTypeUseCase.LoveTypeTestResult> createInvalidLoveTypeTestResults() {
        return List.of(
                UpdateMemberLoveTypeUseCase.LoveTypeTestResult.builder()
                        .questionId(999L) // 존재하지 않는 질문 ID
                        .score(1)
                        .build()
        );
    }
}