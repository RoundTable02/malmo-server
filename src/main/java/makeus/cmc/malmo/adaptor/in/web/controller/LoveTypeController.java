package makeus.cmc.malmo.adaptor.in.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import makeus.cmc.malmo.adaptor.in.web.docs.ApiCommonResponses;
import makeus.cmc.malmo.adaptor.in.web.docs.SwaggerResponses;
import makeus.cmc.malmo.adaptor.in.web.dto.BaseListResponse;
import makeus.cmc.malmo.adaptor.in.web.dto.BaseResponse;
import makeus.cmc.malmo.application.port.in.GetLoveTypeQuestionsUseCase;
import makeus.cmc.malmo.application.port.in.GetLoveTypeUseCase;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "애착유형 검사 API", description = "애착유형 검사 결과 등록 API")
@Slf4j
@RestController
@RequestMapping("/love-types")
@RequiredArgsConstructor
public class LoveTypeController {

    private final GetLoveTypeQuestionsUseCase getLoveTypeQuestionsUseCase;
    private final GetLoveTypeUseCase getLoveTypeUseCase;

    @Operation(
            summary = "애착 유형 검사 질문 조회",
            description = "애착 유형 검사의 질문을 조회합니다. JWT 토큰이 필요합니다.",
            security = @SecurityRequirement(name = "Bearer Authentication")
    )
    @ApiResponse(
            responseCode = "200",
            description = "애착 유형 조회 성공",
            content = @Content(schema = @Schema(implementation = SwaggerResponses.LoveTypeQuestionSuccessResponse.class))
    )
    @ApiCommonResponses.RequireAuth
    @GetMapping("/questions")
    public BaseResponse<BaseListResponse<GetLoveTypeQuestionsUseCase.LoveTypeQuestionDto>> getLoveTypeQuestions() {
        GetLoveTypeQuestionsUseCase.LoveTypeQuestionsResponseDto loveTypeQuestions
                = getLoveTypeQuestionsUseCase.getLoveTypeQuestions();

        return BaseListResponse.success(loveTypeQuestions.getList());
    }

    @Operation(
            summary = "애착 유형 조회",
            description = "애착 유형의 내용을 조회합니다. JWT 토큰이 필요합니다.",
            security = @SecurityRequirement(name = "Bearer Authentication")
    )
    @ApiResponse(
            responseCode = "200",
            description = "애착 유형 조회 성공",
            content = @Content(schema = @Schema(implementation = SwaggerResponses.GetLoveTypeSuccessResponse.class))
    )
    @ApiCommonResponses.RequireAuth
    @GetMapping("/{loveTypeId}")
    public BaseResponse<GetLoveTypeUseCase.GetLoveTypeResponseDto> getLoveType(@PathVariable Integer loveTypeId) {
        GetLoveTypeUseCase.GetLoveTypeCommand command = GetLoveTypeUseCase.GetLoveTypeCommand.builder()
                .loveTypeId(loveTypeId.longValue())
                .build();
        return BaseResponse.success(getLoveTypeUseCase.getLoveType(command));
    }
}
