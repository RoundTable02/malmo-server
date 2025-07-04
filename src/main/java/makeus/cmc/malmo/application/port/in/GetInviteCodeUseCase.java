package makeus.cmc.malmo.application.port.in;

import lombok.Builder;
import lombok.Data;

public interface GetInviteCodeUseCase {

    InviteCodeResponseDto getInviteCode(InviteCodeCommand command);

    @Data
    @Builder
    class InviteCodeCommand {
        private Long userId;
    }

    @Data
    @Builder
    class InviteCodeResponseDto {
        private String coupleCode;
    }
}
