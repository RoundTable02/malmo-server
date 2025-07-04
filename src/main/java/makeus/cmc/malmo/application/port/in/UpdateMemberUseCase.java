package makeus.cmc.malmo.application.port.in;

import lombok.Builder;
import lombok.Data;
import makeus.cmc.malmo.domain.model.member.MemberState;

public interface UpdateMemberUseCase {

    UpdateMemberResponseDto updateMember(UpdateMemberCommand command);

    @Data
    @Builder
    class UpdateMemberCommand {
        private Long memberId;
        private String nickname;
        private String email;
    }

    @Data
    @Builder
    class UpdateMemberResponseDto {
        private String nickname;
        private String email;
    }
}
