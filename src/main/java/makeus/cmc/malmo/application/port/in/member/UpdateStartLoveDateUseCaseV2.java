package makeus.cmc.malmo.application.port.in.member;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

/**
 * V2 연애 시작일 변경 UseCase
 * - 커플로 연동된 사용자만 사용할 수 있습니다.
 * - 커플의 startLoveDate만 업데이트합니다.
 * - 개인의 startLoveDate는 더 이상 업데이트하지 않습니다.
 */
public interface UpdateStartLoveDateUseCaseV2 {

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
