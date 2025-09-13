package makeus.cmc.malmo.application.port.out.sse;

import makeus.cmc.malmo.domain.value.id.MemberId;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

public interface ConnectSsePort {
    SseEmitter connect(MemberId memberId);
}

