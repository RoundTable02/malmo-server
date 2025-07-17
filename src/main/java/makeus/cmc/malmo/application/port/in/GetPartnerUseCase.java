package makeus.cmc.malmo.application.port.in;

import lombok.Builder;
import lombok.Data;
import makeus.cmc.malmo.domain.model.member.MemberState;

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
        private String loveTypeTitle;
        private String loveTypeImageUrl;
        private float avoidanceRate;
        private float anxietyRate;
        private String nickname;
    }
}
