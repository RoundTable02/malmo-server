package makeus.cmc.malmo.application.port.in;

import lombok.Builder;
import lombok.Data;
import makeus.cmc.malmo.domain.model.member.MemberState;

import java.time.LocalDate;

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
