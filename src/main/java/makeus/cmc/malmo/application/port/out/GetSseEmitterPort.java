package makeus.cmc.malmo.application.port.out;

import makeus.cmc.malmo.domain.model.value.MemberId;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

public interface GetSseEmitterPort {
    SseEmitter getEmitter(MemberId memberId);
}
