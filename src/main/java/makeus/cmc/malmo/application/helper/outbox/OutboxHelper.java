package makeus.cmc.malmo.application.helper.outbox;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import makeus.cmc.malmo.adaptor.message.StreamMessage;
import makeus.cmc.malmo.adaptor.message.StreamMessageType;
import makeus.cmc.malmo.adaptor.out.persistence.entity.OutboxEntity;
import makeus.cmc.malmo.application.port.out.SaveOutboxPort;
import makeus.cmc.malmo.domain.model.Outbox;
import makeus.cmc.malmo.domain.value.state.OutboxState;
import makeus.cmc.malmo.adaptor.out.persistence.repository.OutboxRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class OutboxHelper {

    private final ObjectMapper objectMapper;

    private final ApplicationEventPublisher eventPublisher;
    private final SaveOutboxPort saveOutboxPort;

    @Transactional
    public void publish(StreamMessageType type, StreamMessage payload) {
        String jsonPayload;
        try {
            jsonPayload = objectMapper.writeValueAsString(payload);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize payload", e);
        }

        Outbox outbox = Outbox.create(type.name(), jsonPayload);
        saveOutboxPort.save(outbox);

        // 트랜잭션이 성공적으로 커밋되면 OutboxEvent를 발행하도록 요청
        eventPublisher.publishEvent(new OutboxMessageSavedEvent(this, outbox.getId()));
        log.info("Saved message to Outbox and published event for outboxId: {}", outbox.getId());
    }
}
