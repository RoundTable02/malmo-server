package makeus.cmc.malmo.adaptor.in.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import makeus.cmc.malmo.adaptor.in.web.docs.SwaggerErrorResponse;
import makeus.cmc.malmo.adaptor.in.web.docs.SwaggerResponses;
import makeus.cmc.malmo.adaptor.in.web.dto.BaseListResponse;
import makeus.cmc.malmo.adaptor.in.web.dto.BaseResponse;
import makeus.cmc.malmo.application.port.in.CalculateQuestionResultUseCase;
import makeus.cmc.malmo.application.port.in.GetLoveTypeQuestionResultUseCase;
import makeus.cmc.malmo.application.port.in.GetLoveTypeQuestionsUseCase;
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
    private final GetLoveTypeQuestionResultUseCase getLoveTypeQuestionResultUseCase;

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
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "500",
                    description = "서버 내부 오류",
                    content = @Content(schema = @Schema(implementation = SwaggerErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "존재하지 않는 애착 유형 검사 질문",
                    content = @Content(schema = @Schema(implementation = SwaggerErrorResponse.class))
            )
    })
    @PostMapping("/result")
    public BaseResponse<CalculateQuestionResultUseCase.CalculateResultResponse> registerResult(
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

    @Operation(
            summary = "애착 유형 검사 결과 조회",
            description = "애착 유형 검사 답변의 결과를 조회합니다. 답변 등록 시와 동일한 결과를 반환합니다."
    )
    @ApiResponse(
            responseCode = "200",
            description = "애착 유형 결과 조회 성공",
            content = @Content(schema = @Schema(implementation = SwaggerResponses.LoveTypeQuestionCalculateSuccessResponse.class))
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "500",
                    description = "서버 내부 오류",
                    content = @Content(schema = @Schema(implementation = SwaggerErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "존재하지 않는 애착 유형 결과",
                    content = @Content(schema = @Schema(implementation = SwaggerErrorResponse.class))
            )
    })
    @GetMapping("/result/{loveTypeId}")
    public BaseResponse<GetLoveTypeQuestionResultUseCase.LoveTypeResultResponse> getLoveTypeResult(
            @PathVariable Long loveTypeId
    ) {
        GetLoveTypeQuestionResultUseCase.GetLoveTypeResultCommand command = GetLoveTypeQuestionResultUseCase.GetLoveTypeResultCommand.builder()
                .loveTypeId(loveTypeId)
                .build();

        return BaseResponse.success(getLoveTypeQuestionResultUseCase.getResult(command));
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
