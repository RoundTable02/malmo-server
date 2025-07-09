package makeus.cmc.malmo.application.port.in;

import lombok.Builder;
import lombok.Data;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

public interface ConnectSseUseCase {

    SseConnectionResponse connectSse(SseConnectionCommand command);

    @Data
    @Builder
    class SseConnectionCommand {
        private Long userId;
        private String coupleCode;
    }

    @Data
    @Builder
    class SseConnectionResponse {
        private SseEmitter sseEmitter;
    }
}
