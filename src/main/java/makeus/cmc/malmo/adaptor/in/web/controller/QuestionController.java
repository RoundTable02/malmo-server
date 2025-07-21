package makeus.cmc.malmo.adaptor.in.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import makeus.cmc.malmo.adaptor.in.web.docs.ApiCommonResponses;
import makeus.cmc.malmo.adaptor.in.web.docs.SwaggerResponses;
import makeus.cmc.malmo.adaptor.in.web.dto.BaseListResponse;
import makeus.cmc.malmo.adaptor.in.web.dto.BaseResponse;
import makeus.cmc.malmo.application.port.in.GetQuestionUseCase;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@Tag(name = "오늘의 질문 API", description = "커플 오늘의 질문 API")
@Slf4j
@RestController
@RequestMapping("/questions")
@RequiredArgsConstructor
public class QuestionController {

    private final GetQuestionUseCase getQuestionUseCase;

    @Operation(
            summary = "오늘의 질문 조회",
            description = "커플 오늘의 질문을 조회합니다. JWT 토큰이 필요합니다.",
            security = @SecurityRequirement(name = "Bearer Authentication")
    )
    @ApiResponse(
            responseCode = "200",
            description = "오늘의 질문 조회 성공",
            content = @Content(schema = @Schema(implementation = SwaggerResponses.QuestionSuccessResponse.class))
    )
    @ApiCommonResponses.RequireAuth
    @GetMapping("/today")
    public BaseResponse<GetQuestionUseCase.GetQuestionResponse> getTodayQuestion(
            @AuthenticationPrincipal User user
    ) {
        GetQuestionUseCase.GetTodayQuestionCommand command = GetQuestionUseCase.GetTodayQuestionCommand.builder()
                .userId(Long.valueOf(user.getUsername()))
                .build();

        return BaseResponse.success(getQuestionUseCase.getTodayQuestion(command));
    }

    @Operation(
            summary = "과거 질문 조회",
            description = "커플 오늘의 질문을 조회합니다. JWT 토큰이 필요합니다.",
            security = @SecurityRequirement(name = "Bearer Authentication")
    )
    @ApiResponse(
            responseCode = "200",
            description = "질문 내용 조회 성공",
            content = @Content(schema = @Schema(implementation = SwaggerResponses.PastQuestionSuccessResponse.class))
    )
    @ApiCommonResponses.OnlyCouple
    @ApiCommonResponses.RequireAuth
    @GetMapping("/{level}")
    public BaseResponse<PastQuestionResponseDto> getQuestion(
            @AuthenticationPrincipal User user,
            @PathVariable int level) {
        return BaseResponse.success(PastQuestionResponseDto.builder().build());
    }

    @Operation(
            summary = "오늘의 질문 답변 등록",
            description = "커플 오늘의 질문에 답변을 등록합니다. JWT 토큰이 필요합니다.",
            security = @SecurityRequirement(name = "Bearer Authentication")
    )
    @ApiResponse(
            responseCode = "200",
            description = "질문 답변 등록 성공",
            content = @Content(schema = @Schema(implementation = SwaggerResponses.AnswerSuccessResponse.class))
    )
    @ApiCommonResponses.OnlyCouple
    @ApiCommonResponses.RequireAuth
    @PostMapping("/{coupleQuestionId}/answers")
    public BaseResponse<AnswerResponseDto> postAnswer(
            @AuthenticationPrincipal User user,
            @PathVariable Long coupleQuestionId,
            @Valid @RequestBody AnswerRequestDto requestDto
    ) {
        return BaseResponse.success(AnswerResponseDto.builder().build());
    }

    @Operation(
            summary = "질문 답변 조회",
            description = "커플 질문 답변을 조회합니다. JWT 토큰이 필요합니다.",
            security = @SecurityRequirement(name = "Bearer Authentication")
    )
    @ApiResponse(
            responseCode = "200",
            description = "질문 답변 조회 성공",
            content = @Content(schema = @Schema(implementation = SwaggerResponses.PastAnswerSuccessResponse.class))
    )
    @ApiCommonResponses.OnlyCouple
    @ApiCommonResponses.RequireAuth
    @GetMapping("/{coupleQuestionId}/answers")
    public BaseResponse<PastAnswerResponseDto> getAnswers(
            @AuthenticationPrincipal User user,
            @PathVariable String coupleQuestionId) {
        return BaseResponse.success(PastAnswerResponseDto.builder().build());
    }

    @Data
    public static class AnswerRequestDto {
        private Long coupleQuestionId;
        private String answer;
    }

    @Data
    @Builder
    public static class AnswerResponseDto {
        private Long coupleQuestionId;
    }


    @Data
    @Builder
    public static class PastQuestionResponseDto {
        private Long coupleQuestionId;
        private String title;
        private String content;
        private LocalDateTime createdAt;
    }

    @Data
    @Builder
    public static class PastAnswerResponseDto {
        private PastAnswerDto me;
        private PastAnswerDto partner;
    }

    @Data
    @Builder
    public static class PastAnswerDto {
        private String nickname;
        private String answer;
        private boolean updatable;
    }
}
