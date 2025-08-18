package makeus.cmc.malmo.adaptor.in.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import makeus.cmc.malmo.adaptor.in.web.docs.ApiCommonResponses;
import makeus.cmc.malmo.adaptor.in.web.docs.SwaggerResponses;
import makeus.cmc.malmo.adaptor.in.web.dto.BaseListResponse;
import makeus.cmc.malmo.adaptor.in.web.dto.BaseResponse;
import makeus.cmc.malmo.application.port.in.CalculateQuestionResultUseCase;
import makeus.cmc.malmo.application.port.in.GetLoveTypeQuestionsUseCase;
import makeus.cmc.malmo.application.port.in.member.UpdateMemberLoveTypeUseCase;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "애착유형 검사 API", description = "애착유형 검사 결과 등록 API")
@Slf4j
@RestController
@RequestMapping("/love-types")
@RequiredArgsConstructor
public class LoveTypeController {

    private final GetLoveTypeQuestionsUseCase getLoveTypeQuestionsUseCase;
    private final CalculateQuestionResultUseCase calculateQuestionResultUseCase;

    @Operation(
            summary = "애착 유형 검사 질문 조회",
            description = "애착 유형 검사의 질문을 조회합니다."
    )
    @ApiResponse(
            responseCode = "200",
            description = "애착 유형 조회 성공",
            content = @Content(schema = @Schema(implementation = SwaggerResponses.LoveTypeQuestionSuccessResponse.class))
    )
    @GetMapping("/questions")
    public BaseResponse<BaseListResponse<GetLoveTypeQuestionsUseCase.LoveTypeQuestionDto>> getLoveTypeQuestions() {
        GetLoveTypeQuestionsUseCase.LoveTypeQuestionsResponseDto loveTypeQuestions
                = getLoveTypeQuestionsUseCase.getLoveTypeQuestions();

        return BaseListResponse.success(loveTypeQuestions.getList(), loveTypeQuestions.getTotalCount());
    }

    @Operation(
            summary = "애착 유형 검사 질문 답변 및 결과 조회",
            description = "애착 유형 검사 답변의 결과를 조회합니다."
    )
    @ApiResponse(
            responseCode = "200",
            description = "애착 유형 등록 성공",
            content = @Content(schema = @Schema(implementation = SwaggerResponses.LoveTypeQuestionCalculateSuccessResponse.class))
    )
    @PostMapping("/result")
    public BaseResponse<CalculateQuestionResultUseCase.CalculateResultResponse> getLoveTypeQuestions(
            @Valid @RequestBody RegisterLoveTypeRequestDto requestDto
    ) {
        List<CalculateQuestionResultUseCase.LoveTypeTestResult> results = requestDto.getResults().stream()
                .map(result -> CalculateQuestionResultUseCase.LoveTypeTestResult.builder()
                        .questionId(result.getQuestionId())
                        .score(result.getScore())
                        .build())
                .toList();

        CalculateQuestionResultUseCase.UpdateMemberLoveTypeCommand command =
                CalculateQuestionResultUseCase.UpdateMemberLoveTypeCommand.builder()
                        .results(results)
                        .build();

        return BaseResponse.success(calculateQuestionResultUseCase.calculateResult(command));
    }

    @Data
    public static class RegisterLoveTypeRequestDto {
        @Valid
        private List<LoveTypeTestResult> results;
    }

    @Data
    public static class LoveTypeTestResult {
        @NotNull(message = "질문 ID는 필수 입력값입니다.")
        private Long questionId;
        @NotNull(message = "점수는 필수 입력값입니다.")
        @Max(5) @Min(1)
        private Integer score;
    }
}
