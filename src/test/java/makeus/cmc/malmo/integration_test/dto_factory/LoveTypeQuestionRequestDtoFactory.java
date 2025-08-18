package makeus.cmc.malmo.integration_test.dto_factory;

import makeus.cmc.malmo.adaptor.in.web.controller.LoveTypeController;
import makeus.cmc.malmo.adaptor.in.web.controller.MemberController;

import java.util.ArrayList;
import java.util.List;

public class LoveTypeQuestionRequestDtoFactory {
    public static LoveTypeController.RegisterLoveTypeRequestDto createRegisterLoveTypeRequestDto(int[] scores) {
        LoveTypeController.RegisterLoveTypeRequestDto dto = new LoveTypeController.RegisterLoveTypeRequestDto();
        List<LoveTypeController.LoveTypeTestResult> results = new ArrayList<>();

        for (int i = 1; i < scores.length + 1; i++) {
            LoveTypeController.LoveTypeTestResult loveTypeTestResult = new LoveTypeController.LoveTypeTestResult();
            loveTypeTestResult.setQuestionId((long) i);
            loveTypeTestResult.setScore(scores[i - 1]);
            results.add(loveTypeTestResult);
        }

        dto.setResults(results);
        return dto;
    }
}
