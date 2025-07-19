package makeus.cmc.malmo.application.port.in;

import lombok.Builder;
import lombok.Data;
import makeus.cmc.malmo.domain.model.love_type.LoveTypeCategory;

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
        private LoveTypeCategory loveTypeCategory;
    }
}
