package makeus.cmc.malmo.service;

import makeus.cmc.malmo.application.port.in.UpdateMemberLoveTypeUseCase;
import makeus.cmc.malmo.application.port.out.SaveMemberPort;
import makeus.cmc.malmo.application.service.LoveTypeService;
import makeus.cmc.malmo.domain.exception.LoveTypeNotFoundException;
import makeus.cmc.malmo.domain.exception.LoveTypeQuestionNotFoundException;
import makeus.cmc.malmo.domain.exception.MemberNotFoundException;
import makeus.cmc.malmo.domain.model.love_type.LoveTypeCategory;
import makeus.cmc.malmo.domain.model.member.Member;
import makeus.cmc.malmo.domain.model.value.MemberId;
import makeus.cmc.malmo.domain.service.LoveTypeDataService;
import makeus.cmc.malmo.domain.service.MemberDomainService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("LoveTypeService 단위 테스트")
class LoveTypeServiceTest {

    @Mock
    private MemberDomainService memberDomainService;

    @Mock
    private LoveTypeDataService loveTypeDataService;

    @Mock
    private SaveMemberPort saveMemberPort;

    @InjectMocks
    private LoveTypeService loveTypeService;

    @Nested
    @DisplayName("회원 애착 유형 업데이트 기능")
    class UpdateMemberLoveTypeFeature {

        @Test
        @DisplayName("성공: 유효한 회원 ID와 테스트 결과로 애착 유형 업데이트가 성공한다")
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
            LoveTypeDataService.LoveTypeCalculationResult calculationResult = 
                    new LoveTypeDataService.LoveTypeCalculationResult(LoveTypeCategory.STABLE_TYPE, 2.5f, 1.8f);

            given(memberDomainService.getMemberById(MemberId.of(memberId))).willReturn(member);
            given(loveTypeDataService.findLoveTypeCategoryByTestResult(anyList())).willReturn(calculationResult);
            given(saveMemberPort.saveMember(member)).willReturn(member);

            // When
            loveTypeService.updateMemberLoveType(command);

            // Then
            then(memberDomainService).should().getMemberById(MemberId.of(memberId));
            then(loveTypeDataService).should().findLoveTypeCategoryByTestResult(anyList());
            then(member).should().updateLoveTypeId(eq(LoveTypeCategory.STABLE_TYPE), eq(2.5f), eq(1.8f));
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

            given(memberDomainService.getMemberById(MemberId.of(nonExistentMemberId)))
                    .willThrow(new MemberNotFoundException());

            // When & Then
            assertThatThrownBy(() -> loveTypeService.updateMemberLoveType(command))
                    .isInstanceOf(MemberNotFoundException.class);

            then(memberDomainService).should().getMemberById(MemberId.of(nonExistentMemberId));
            then(loveTypeDataService).should(never()).findLoveTypeCategoryByTestResult(anyList());
            then(saveMemberPort).should(never()).saveMember(any());
        }

        @Test
        @DisplayName("실패: 테스트 결과 계산 중 질문 검증 실패 시 LoveTypeQuestionNotFoundException이 발생한다")
        void givenInvalidTestResults_whenUpdateMemberLoveType_thenThrowLoveTypeQuestionNotFoundException() {
            // Given
            Long memberId = 1L;
            List<UpdateMemberLoveTypeUseCase.LoveTypeTestResult> testResults = createInvalidLoveTypeTestResults();
            UpdateMemberLoveTypeUseCase.UpdateMemberLoveTypeCommand command = 
                    UpdateMemberLoveTypeUseCase.UpdateMemberLoveTypeCommand.builder()
                            .memberId(memberId)
                            .results(testResults)
                            .build();

            Member member = mock(Member.class);

            given(memberDomainService.getMemberById(MemberId.of(memberId))).willReturn(member);
            given(loveTypeDataService.findLoveTypeCategoryByTestResult(anyList()))
                    .willThrow(new LoveTypeQuestionNotFoundException());

            // When & Then
            assertThatThrownBy(() -> loveTypeService.updateMemberLoveType(command))
                    .isInstanceOf(LoveTypeQuestionNotFoundException.class);

            then(memberDomainService).should().getMemberById(MemberId.of(memberId));
            then(loveTypeDataService).should().findLoveTypeCategoryByTestResult(anyList());
            then(saveMemberPort).should(never()).saveMember(any());
        }

        @Test
        @DisplayName("실패: 계산된 애착 유형을 찾을 수 없을 때 LoveTypeNotFoundException이 발생한다")
        void givenCalculatedLoveTypeNotFound_whenUpdateMemberLoveType_thenThrowLoveTypeNotFoundException() {
            // Given
            Long memberId = 1L;
            List<UpdateMemberLoveTypeUseCase.LoveTypeTestResult> testResults = createLoveTypeTestResults();
            UpdateMemberLoveTypeUseCase.UpdateMemberLoveTypeCommand command = 
                    UpdateMemberLoveTypeUseCase.UpdateMemberLoveTypeCommand.builder()
                            .memberId(memberId)
                            .results(testResults)
                            .build();

            Member member = mock(Member.class);

            given(memberDomainService.getMemberById(MemberId.of(memberId))).willReturn(member);
            given(loveTypeDataService.findLoveTypeCategoryByTestResult(anyList()))
                    .willThrow(new LoveTypeNotFoundException());

            // When & Then
            assertThatThrownBy(() -> loveTypeService.updateMemberLoveType(command))
                    .isInstanceOf(LoveTypeNotFoundException.class);

            then(memberDomainService).should().getMemberById(MemberId.of(memberId));
            then(loveTypeDataService).should().findLoveTypeCategoryByTestResult(anyList());
            then(saveMemberPort).should(never()).saveMember(any());
        }
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