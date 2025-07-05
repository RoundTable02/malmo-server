package makeus.cmc.malmo.application.port.in;

import lombok.Builder;
import lombok.Data;

import java.util.List;

public interface UpdateMemberLoveTypeUseCase {

    RegisterLoveTypeResponseDto updateMemberLoveType(UpdateMemberLoveTypeCommand command);

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

    @Data
    @Builder
    class RegisterLoveTypeResponseDto {
        private Long loveTypeId;
        private String title;
        private String summary;
        private String content;
        private String imageUrl;
    }
}
