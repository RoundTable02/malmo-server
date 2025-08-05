package makeus.cmc.malmo.application.port.in.member;

import lombok.Builder;
import lombok.Data;

public interface LogOutUseCase {

    void logout(LogOutCommand command);

    @Data
    @Builder
    class LogOutCommand {
        private Long userId;
    }
}