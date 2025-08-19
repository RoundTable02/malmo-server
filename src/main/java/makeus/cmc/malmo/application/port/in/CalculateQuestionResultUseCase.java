package makeus.cmc.malmo.application.port.in;

import lombok.Builder;
import lombok.Data;
import makeus.cmc.malmo.domain.value.type.LoveTypeCategory;

import java.util.List;

public interface CalculateQuestionResultUseCase {

    CalculateResultResponse calculateResult(UpdateMemberLoveTypeCommand command);

    @Data
    @Builder
    class UpdateMemberLoveTypeCommand {
        private List<LoveTypeTestResult> results;
    }

    @Data
    @Builder
    class LoveTypeTestResult {
        private Long questionId;
        private Integer score;
    }

    @Data
    @Builder
    class CalculateResultResponse {
        private Long loveTypeId;
        private LoveTypeCategory loveTypeCategory;
        private float avoidanceRate;
        private float anxietyRate;
    }
}
