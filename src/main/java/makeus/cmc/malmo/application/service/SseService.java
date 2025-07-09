package makeus.cmc.malmo.application.service;

import lombok.RequiredArgsConstructor;
import makeus.cmc.malmo.application.port.in.ConnectSseUseCase;
import makeus.cmc.malmo.application.port.out.ConnectSsePort;
import makeus.cmc.malmo.domain.model.value.MemberId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class SseService implements ConnectSseUseCase {

    private final ConnectSsePort connectSsePort;

    @Override
    public SseConnectionResponse connectSse(SseConnectionCommand command) {
        MemberId memberId = MemberId.of(command.getUserId());
        SseEmitter emitter = connectSsePort.connect(memberId);
        return SseConnectionResponse.builder()
                .sseEmitter(emitter)
                .build();
    }
}
