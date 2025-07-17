package makeus.cmc.malmo.application.port.in;

import lombok.Builder;
import lombok.Data;

import java.util.List;

public interface UpdateMemberLoveTypeUseCase {

    void updateMemberLoveType(UpdateMemberLoveTypeCommand command);

    @Data
    @Builder
    class UpdateMemberLoveTypeCommand {
        private Long memberId;
        private List<LoveTypeTestResult> results;
    }

    @Data
    @Builder
    class LoveTypeTestResult {
        private Long questionId;
        private Integer score;
    }
}
