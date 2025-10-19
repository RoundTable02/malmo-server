package makeus.cmc.malmo.application.port.out.amplitude;

import lombok.Builder;
import lombok.Data;

public interface AmplitudePort {

    void identifyUser(IdentifyUserCommand command);

    @Data
    @Builder
    class IdentifyUserCommand {
        private String userId;
        private String deviceId;
        private String email;
        private String nickname;
    }
}
