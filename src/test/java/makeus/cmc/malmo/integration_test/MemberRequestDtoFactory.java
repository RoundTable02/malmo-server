package makeus.cmc.malmo.integration_test;

import makeus.cmc.malmo.adaptor.in.web.controller.SignUpController;

import java.time.LocalDate;
import java.util.List;

public class MemberRequestDtoFactory {
    public static SignUpController.SignUpRequestDto createSignUpRequestDto(List<SignUpController.TermsDto> terms,
                                                                           String nickname,
                                                                           LocalDate startLoveDate) {
        SignUpController.SignUpRequestDto dto = new SignUpController.SignUpRequestDto();
        dto.setTerms(terms);
        dto.setNickname(nickname);
        dto.setLoveStartDate(startLoveDate);

        return dto;
    }

    public static SignUpController.TermsDto createTermsDto(Long termsId, boolean isAgreed) {
        SignUpController.TermsDto termsDto = new SignUpController.TermsDto();
        termsDto.setTermsId(termsId);
        termsDto.setIsAgreed(isAgreed);

        return termsDto;
    }
}
