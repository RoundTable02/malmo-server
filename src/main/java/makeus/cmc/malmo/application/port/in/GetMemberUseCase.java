package makeus.cmc.malmo.application.port.in;

import lombok.Builder;
import lombok.Data;
import makeus.cmc.malmo.domain.model.member.MemberState;

public interface GetMemberUseCase {

    MemberResponseDto getMemberInfo(MemberInfoCommand command);

    @Data
    @Builder
    class MemberInfoCommand {
        private Long userId;
    }

    @Data
    @Builder
    class MemberResponseDto {
        private MemberState memberState;
        private String loveTypeTitle;
        private float avoidanceRate;
        private float anxietyRate;
        private String nickname;
        private String email;
    }
}
