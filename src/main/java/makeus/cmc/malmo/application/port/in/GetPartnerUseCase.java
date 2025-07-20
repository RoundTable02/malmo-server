package makeus.cmc.malmo.application.port.in;

import lombok.Builder;
import lombok.Data;
import makeus.cmc.malmo.domain.model.love_type.LoveTypeCategory;
import makeus.cmc.malmo.domain.model.member.MemberState;
import makeus.cmc.malmo.domain.value.state.MemberState;

public interface GetPartnerUseCase {

    PartnerMemberResponseDto getPartnerInfo(PartnerInfoCommand command);

    @Data
    @Builder
    class PartnerInfoCommand {
        private Long userId;
    }

    @Data
    @Builder
    class PartnerMemberResponseDto {
        private MemberState memberState;
        private LoveTypeCategory loveTypeCategory;
        private float avoidanceRate;
        private float anxietyRate;
        private String nickname;
    }
}
