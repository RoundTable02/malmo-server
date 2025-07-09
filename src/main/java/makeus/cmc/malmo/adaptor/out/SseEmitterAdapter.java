package makeus.cmc.malmo.adaptor.out;

import makeus.cmc.malmo.adaptor.out.exception.SseConnectionException;
import makeus.cmc.malmo.application.port.out.ConnectSsePort;
import makeus.cmc.malmo.application.port.out.SendSseEventPort;

import lombok.extern.slf4j.Slf4j;
import makeus.cmc.malmo.domain.model.value.MemberId;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class SseEmitterAdapter implements SendSseEventPort, ConnectSsePort {
    private static final long TIMEOUT = 10 * 60 * 1000L; // 10분
    private static final int MAX_SIZE = 1000;
    public static final long RECONNECT_TIME_MILLIS = 3000L;

    private final Map<Long, SseEmitter> emitters = new ConcurrentHashMap<>();

    @Override
    public SseEmitter connect(MemberId memberId) {
        if (emitters.size() >= MAX_SIZE) {
            log.warn("Cannot connect SSE: Emitter map is full (size: {}).", emitters.size());
            throw new SseConnectionException("Maximum number of connections exceeded.");
        }

        Long memberIdValue = memberId.getValue();
        SseEmitter newEmitter = new SseEmitter(TIMEOUT);

        SseEmitter oldEmitter = emitters.put(memberIdValue, newEmitter);
        if (oldEmitter != null) {
            oldEmitter.complete();
        }

        newEmitter.onTimeout(() -> {
            log.info("SSE emitter timed out for member: {}", memberIdValue);
            emitters.remove(memberIdValue, newEmitter);
        });
        newEmitter.onCompletion(() -> {
            log.info("SSE emitter completed for member: {}", memberIdValue);
            emitters.remove(memberIdValue, newEmitter);
        });
        newEmitter.onError(e -> {
            log.error("SSE emitter error for member: {}", memberIdValue, e);
            emitters.remove(memberIdValue, newEmitter);
        });

        try {
            newEmitter.send(SseEmitter.event()
                    .id(String.valueOf(memberIdValue))
                    .name("connected")
                    .data("SSE connection established.")
                    .reconnectTime(RECONNECT_TIME_MILLIS));
        } catch (IOException e) {
            log.error("Failed to send initial SSE connection event for member: {}", memberIdValue, e);
            newEmitter.complete();
        }

        return newEmitter;
    }

    @Override
    public void sendToMember(MemberId memberId, NotificationEvent event) {
        Long memberIdValue = memberId.getValue();
        SseEmitter emitter = emitters.get(memberIdValue);
        if (emitter == null) {
            log.debug("SSE emitter not found for member: {}", memberIdValue);
            return;
        }

        try {
            emitter.send(SseEmitter.event()
                    .id(memberIdValue + "_" + System.currentTimeMillis()) // 각 이벤트에 고유 ID 부여
                    .name(event.getEventType().getEventName())
                    .data(event.getData()));
        } catch (IOException | IllegalStateException e) {
            log.error("Failed to send SSE event to member: {}. Removing emitter.", memberIdValue, e);
            emitter.complete();
        }
    }
}
