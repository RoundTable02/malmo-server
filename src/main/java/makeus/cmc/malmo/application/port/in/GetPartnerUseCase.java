package makeus.cmc.malmo.application.port.in;

import lombok.Builder;
import lombok.Data;
import makeus.cmc.malmo.domain.model.member.MemberState;

import java.time.LocalDate;

public interface GetPartnerUseCase {

    PartnerMemberResponseDto getMemberInfo(PartnerInfoCommand command);

    @Data
    @Builder
    class PartnerInfoCommand {
        private Long userId;
    }

    @Data
    @Builder
    class PartnerMemberResponseDto {
        private MemberState memberState;
        private Long loveTypeId;
        private String loveTypeTitle;
        private float avoidanceRate;
        private float anxietyRate;
        private String nickname;
    }
}
