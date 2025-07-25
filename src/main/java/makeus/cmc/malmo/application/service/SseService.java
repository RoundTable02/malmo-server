package makeus.cmc.malmo.application.service;

import lombok.RequiredArgsConstructor;
import makeus.cmc.malmo.adaptor.in.aop.CheckValidMember;
import makeus.cmc.malmo.application.port.in.ConnectSseUseCase;
import makeus.cmc.malmo.application.port.out.ConnectSsePort;
import makeus.cmc.malmo.domain.value.id.MemberId;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Service
@RequiredArgsConstructor
public class SseService implements ConnectSseUseCase {

    private final ConnectSsePort connectSsePort;

    @Override
    @CheckValidMember
    public SseConnectionResponse connectSse(SseConnectionCommand command) {
        MemberId memberId = MemberId.of(command.getUserId());
        SseEmitter emitter = connectSsePort.connect(memberId);
        return SseConnectionResponse.builder()
                .sseEmitter(emitter)
                .build();
    }
}
