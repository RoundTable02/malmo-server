package makeus.cmc.malmo.application.port.in;

import lombok.Builder;
import lombok.Data;
import makeus.cmc.malmo.domain.value.type.LoveTypeCategory;

public interface GetLoveTypeQuestionResultUseCase {

    LoveTypeResultResponse getResult(GetLoveTypeResultCommand command);

    @Data
    @Builder
    class GetLoveTypeResultCommand {
        private Long loveTypeId;
    }

    @Data
    @Builder
    class LoveTypeResultResponse {
        private Long loveTypeId;
        private LoveTypeCategory loveTypeCategory;
        private float avoidanceRate;
        private float anxietyRate;
    }
}
