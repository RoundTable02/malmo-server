package makeus.cmc.malmo.adaptor.in.web.controller;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import makeus.cmc.malmo.adaptor.in.web.dto.BaseResponse;
import makeus.cmc.malmo.application.port.in.SignUpUseCase;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class SignUpController {

    private final SignUpUseCase signUpUseCase;

    @PostMapping("/sign-up")
    public BaseResponse<SignUpUseCase.SignUpResponse> signUp(
            @AuthenticationPrincipal User user,
            @RequestBody SignUpRequestDto requestDto
    ) {
        List<SignUpUseCase.TermsCommand> termsCommandList = requestDto.getTerms().stream()
                .map(term -> SignUpUseCase.TermsCommand.builder()
                        .termsId(term.getTermsId())
                        .isAgreed(term.getIsAgreed())
                        .build())
                .toList();

        SignUpUseCase.SignUpCommand command = SignUpUseCase.SignUpCommand.builder()
                .memberId(Long.valueOf(user.getUsername()))
                .terms(termsCommandList)
                .nickname(requestDto.getNickname())
                .loveStartDate(requestDto.getLoveStartDate())
                .build();

        return BaseResponse.success(signUpUseCase.signUp(command));
    }

    @Data
    public static class SignUpRequestDto {
        private List<TermsDto> terms;
        private String nickname;
        private LocalDate loveStartDate;
    }

    @Data
    class TermsDto {
        private Long termsId;
        private Boolean isAgreed;
    }
}
