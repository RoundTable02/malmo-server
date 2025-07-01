package makeus.cmc.malmo.adaptor.in.web.controller;

import lombok.RequiredArgsConstructor;
import makeus.cmc.malmo.adaptor.in.web.dto.BaseListResponse;
import makeus.cmc.malmo.adaptor.in.web.dto.BaseResponse;
import makeus.cmc.malmo.application.port.in.TermsUseCase;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class TermsController {

    private final TermsUseCase termsUseCase;

    @GetMapping("/terms")
    public BaseResponse<BaseListResponse<TermsUseCase.TermsResponse>> getTerms() {

        return BaseListResponse.success(termsUseCase.getTerms());
    }
}
