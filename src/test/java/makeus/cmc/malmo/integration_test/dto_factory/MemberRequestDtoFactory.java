package makeus.cmc.malmo.integration_test.dto_factory;

import makeus.cmc.malmo.adaptor.in.web.controller.MemberController;
import makeus.cmc.malmo.adaptor.in.web.controller.SignUpController;

import java.time.LocalDate;
import java.util.ArrayList;
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

    public static MemberController.UpdateMemberRequestDto createUpdateMemberRequestDto(String nickname) {
        MemberController.UpdateMemberRequestDto dto = new MemberController.UpdateMemberRequestDto();
        dto.setNickname(nickname);
        return dto;
    }

    public static MemberController.UpdateStartLoveDateRequestDto createUpdateStartLoveDateRequestDto(LocalDate startLoveDate) {
        MemberController.UpdateStartLoveDateRequestDto dto = new MemberController.UpdateStartLoveDateRequestDto();
        dto.setStartLoveDate(startLoveDate);
        return dto;
    }

    public static MemberController.RegisterLoveTypeRequestDto createRegisterLoveTypeRequestDto(int[] scores) {
        MemberController.RegisterLoveTypeRequestDto dto = new MemberController.RegisterLoveTypeRequestDto();
        List<MemberController.LoveTypeTestResult> results = new ArrayList<>();

        for (int i = 1; i < scores.length + 1; i++) {
            MemberController.LoveTypeTestResult loveTypeTestResult = new MemberController.LoveTypeTestResult();
            loveTypeTestResult.setQuestionId((long) i);
            loveTypeTestResult.setScore(scores[i - 1]);
            results.add(loveTypeTestResult);
        }

        dto.setResults(results);
        return dto;
    }
}
