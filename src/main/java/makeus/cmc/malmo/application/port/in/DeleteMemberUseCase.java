package makeus.cmc.malmo.application.port.in;

import lombok.Builder;
import lombok.Data;

public interface DeleteMemberUseCase {

    void deleteMember(DeleteMemberCommand command);

    @Data
    @Builder
    class DeleteMemberCommand {
        private Long memberId;
    }
}
