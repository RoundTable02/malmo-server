package makeus.cmc.malmo.adaptor.out;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import makeus.cmc.malmo.adaptor.message.StreamMessage;
import makeus.cmc.malmo.adaptor.message.StreamMessageType;
import makeus.cmc.malmo.adaptor.out.persistence.entity.OutboxEntity;
import makeus.cmc.malmo.adaptor.out.persistence.entity.OutboxState;
import makeus.cmc.malmo.adaptor.out.persistence.repository.OutboxRepository;
import makeus.cmc.malmo.application.port.out.chat.PublishStreamMessagePort;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.connection.stream.RecordId;
import org.springframework.data.redis.core.StreamOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.HashMap;
import java.util.Map;


@Slf4j
@Service
@RequiredArgsConstructor
public class RedisStreamAdapter implements PublishStreamMessagePort {

    @Value("${spring.data.redis.stream-key}")
    private String streamKey;

    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;

    private final OutboxRepository outboxRepository;

    public void publish(StreamMessageType type, StreamMessage payload) {
        try {
            String jsonPayload = objectMapper.writeValueAsString(payload);

            TransactionSynchronizationManager.registerSynchronization(
                    new TransactionSynchronization() {
                        @Override
                        public void beforeCommit(boolean readOnly) {
                            // 트랜잭션 커밋 전에 Outbox 테이블에 메시지 저장
                            OutboxEntity outbox = OutboxEntity.builder()
                                    .type(type.name())
                                    .payload(jsonPayload)
                                    .retryCount(0)
                                    .state(OutboxState.PENDING)
                                    .build();

                            outboxRepository.save(outbox);
                        }

                        @Override
                        public void afterCommit() {
                            try {
                                Map<String, String> map = new HashMap<>();
                                map.put("type", type.name());
                                map.put("payload", jsonPayload);
                                map.put("retry", "0");

                                StreamOperations<String, String, String> ops = redisTemplate.opsForStream();
                                RecordId id = ops.add(MapRecord.create(streamKey, map));

                                log.info("Published message to Redis Stream: type={}, payload={}, id={}", type, payload, id);
                            } catch (Exception e) {
                                throw new RuntimeException("Failed to serialize payload", e);
                            }
                        }
                    }
            );
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
