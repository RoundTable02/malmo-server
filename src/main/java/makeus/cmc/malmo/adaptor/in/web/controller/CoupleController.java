package makeus.cmc.malmo.adaptor.in.web.controller;

import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import makeus.cmc.malmo.adaptor.in.web.dto.BaseResponse;
import makeus.cmc.malmo.application.port.in.CoupleLinkUseCase;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController("/couple")
@RequiredArgsConstructor
public class CoupleController {

    private final CoupleLinkUseCase coupleLinkUseCase;

    @PostMapping
    public BaseResponse<CoupleLinkUseCase.CoupleLinkResponse> linkCouple(
            @AuthenticationPrincipal User user,
            @RequestBody CoupleLinkRequestDto requestDto
    ) {
        CoupleLinkUseCase.CoupleLinkCommand command = CoupleLinkUseCase.CoupleLinkCommand.builder()
                .coupleCode(requestDto.getCoupleCode())
                .userId(user.getUsername())
                .build();
        return BaseResponse.success(coupleLinkUseCase.coupleLink(command));
    }


    @Data
    @Builder
    public static class CoupleLinkRequestDto {
        private String coupleCode;
    }
}
