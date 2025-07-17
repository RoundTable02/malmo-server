package makeus.cmc.malmo.application.port.in;

import lombok.Builder;
import lombok.Data;

public interface GetMemberLoveTypeDetailsUseCase {

    LoveTypeDetailsDto getMemberLoveTypeInfo(MemberLoveTypeCommand command);

    @Data
    @Builder
    class MemberLoveTypeCommand {
        private Long memberId;
    }

    @Data
    @Builder
    class LoveTypeDetailsDto {
        private float memberAnxietyScore;
        private float memberAvoidanceScore;
        private String name;
        private String loveTypeName;
        private String imageUrl;
        private String summary;
        private String description;
        private float anxietyOver;
        private float anxietyUnder;
        private float avoidanceOver;
        private float avoidanceUnder;
        private String[] relationshipAttitudes;
        private String[] problemSolvingAttitudes;
        private String[] emotionalExpressions;
    }
}
