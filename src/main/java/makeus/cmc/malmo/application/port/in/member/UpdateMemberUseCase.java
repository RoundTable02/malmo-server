package makeus.cmc.malmo.application.port.in.member;

import lombok.Builder;
import lombok.Data;

public interface UpdateMemberUseCase {

    UpdateMemberResponseDto updateMember(UpdateMemberCommand command);

    @Data
    @Builder
    class UpdateMemberCommand {
        private Long memberId;
        private String nickname;
    }

    @Data
    @Builder
    class UpdateMemberResponseDto {
        private String nickname;
    }
}
