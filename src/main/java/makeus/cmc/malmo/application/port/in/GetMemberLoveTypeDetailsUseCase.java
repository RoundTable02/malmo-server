package makeus.cmc.malmo.application.port.in;

import lombok.Builder;
import lombok.Data;

public interface GetMemberLoveTypeDetailsUseCase {

    LoveTypeDetailsDto getMemberLoveTypeInfo(MemberLoveTypeCommand command);
    LoveTypeDetailsDto getPartnerLoveTypeInfo(MemberLoveTypeCommand command);

    @Data
    @Builder
    class MemberLoveTypeCommand {
        private Long memberId;
    }

    @Data
    @Builder
    class LoveTypeDetailsDto {
        private Float memberAnxietyScore;
        private Float memberAvoidanceScore;
        private String name;
        private String loveTypeName;
        private String imageUrl;
        private String summary;
        private String description;
        private Float anxietyOver;
        private Float anxietyUnder;
        private Float avoidanceOver;
        private Float avoidanceUnder;
        private String[] relationshipAttitudes;
        private String[] problemSolvingAttitudes;
        private String[] emotionalExpressions;
    }
}
