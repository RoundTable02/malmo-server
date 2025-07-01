package makeus.cmc.malmo.application.port.in;

import lombok.Builder;
import lombok.Data;

public interface CoupleLinkUseCase {

    CoupleLinkResponse coupleLink(CoupleLinkCommand command);

    @Data
    @Builder
    class CoupleLinkCommand {
        private String userId;
        private String coupleCode;
    }

    @Data
    @Builder
    class CoupleLinkResponse {
        private Long coupleId;
    }

}
