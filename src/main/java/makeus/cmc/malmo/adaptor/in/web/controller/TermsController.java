package makeus.cmc.malmo.adaptor.in.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import makeus.cmc.malmo.adaptor.in.web.docs.SwaggerResponses;
import makeus.cmc.malmo.adaptor.in.web.dto.BaseListResponse;
import makeus.cmc.malmo.adaptor.in.web.dto.BaseResponse;
import makeus.cmc.malmo.application.port.in.terms.TermsUseCase;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "약관 관리 API", description = "서비스 이용약관 조회 관련 API")
@RestController
@RequiredArgsConstructor
public class TermsController {

    private final TermsUseCase termsUseCase;

    @Operation(
            summary = "약관 목록 조회",
            description = "서비스 이용약관 목록을 조회합니다. 회원가입 시 동의가 필요한 약관들이 포함됩니다."
    )
    @ApiResponse(
            responseCode = "200",
            description = "약관 목록 조회 성공",
            content = @Content(schema = @Schema(implementation = SwaggerResponses.TermsListSuccessResponse.class))
    )
    @GetMapping("/terms")
    public BaseResponse<BaseListResponse<TermsUseCase.TermsDto>> getTerms() {
        List<TermsUseCase.TermsDto> termsList = termsUseCase.getTerms().getTermsList();
        return BaseListResponse.success(termsList, (long) termsList.size());
    }
}
