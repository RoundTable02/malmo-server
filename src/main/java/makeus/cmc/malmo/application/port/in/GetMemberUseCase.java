package makeus.cmc.malmo.application.port.in;

import lombok.Builder;
import lombok.Data;
import makeus.cmc.malmo.domain.model.member.MemberState;

import java.time.LocalDate;

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
        private LocalDate startLoveDate;

        private String loveTypeTitle;
        private String loveTypeImageUrl;

        private float avoidanceRate;
        private float anxietyRate;
        private String nickname;
        private String email;
    }
}
