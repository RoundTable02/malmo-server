package makeus.cmc.malmo.adaptor.out;

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

    private final Map<Long, SseEmitter> emitters = new ConcurrentHashMap<>();

    @Override
    public void connect(MemberId memberId) {
        if (emitters.size() >= MAX_SIZE) {
            removeRandomEmitter();
        }

        Long memberIdValue = memberId.getValue();
        if (emitters.containsKey(memberIdValue)) {
            emitters.get(memberIdValue).complete();
            emitters.remove(memberIdValue);
        }

        SseEmitter emitter = new SseEmitter(TIMEOUT);
        emitter.onTimeout(() -> {
            try {
                emitter.send(SseEmitter.event()
                        .name("timeout")
                        .data("connection_timeout"));
            } catch (IOException e) {
                log.warn("타임아웃 이벤트 전송 실패", e);
            } finally {
                emitters.remove(memberIdValue);
            }
        });
        emitter.onCompletion(() ->
                emitters.remove(memberIdValue));
        emitter.onError((e) ->
                emitters.remove(memberIdValue));

        try {
            emitter.send(SseEmitter.event()
                            .name("connected")
                            .data("connected"));
        } catch (IOException e) {
            log.error("초기 연결 실패", e);
        }

        emitters.put(memberIdValue, emitter);
    }

    @Override
    public void sendToMember(MemberId memberId, NotificationEvent event) {
        Long memberIdValue = memberId.getValue();
        SseEmitter emitter = emitters.get(memberIdValue);
        if (emitter == null) {
            return;
        }

        try {
            emitter.send(SseEmitter.event()
                    .name(event.getEventName())
                    .data(event.getData()));
        } catch (IOException e) {
            emitters.remove(memberIdValue);
        }
    }

    private void removeRandomEmitter() {
        if (!emitters.isEmpty()) {
            Long key = emitters.keySet().iterator().next();
            emitters.get(key).complete();
            emitters.remove(key);
        }
    }
}
