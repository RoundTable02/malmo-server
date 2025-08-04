package makeus.cmc.malmo.application.port.in.member;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

public interface UpdateStartLoveDateUseCase {

    UpdateStartLoveDateResponse updateStartLoveDate(UpdateStartLoveDateCommand command);

    @Data
    @Builder
    class UpdateStartLoveDateCommand {
        private Long memberId;
        private LocalDate startLoveDate;
    }

    @Data
    @Builder
    class UpdateStartLoveDateResponse {
        private LocalDate startLoveDate;
    }
}
