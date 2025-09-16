package makeus.cmc.malmo.adaptor.out;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import makeus.cmc.malmo.adaptor.message.StreamMessage;
import makeus.cmc.malmo.adaptor.message.StreamMessageType;
import makeus.cmc.malmo.adaptor.out.persistence.entity.OutboxEntity;
import makeus.cmc.malmo.domain.model.Outbox;
import makeus.cmc.malmo.domain.value.state.OutboxState;
import makeus.cmc.malmo.adaptor.out.persistence.repository.OutboxRepository;
import makeus.cmc.malmo.application.port.out.chat.PublishStreamMessagePort;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.connection.stream.RecordId;
import org.springframework.data.redis.connection.stream.StreamRecords;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StreamOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Slf4j
@Service
@RequiredArgsConstructor
public class RedisStreamAdapter implements PublishStreamMessagePort {

    @Value("${spring.data.redis.stream-key}")
    private String streamKey;

    private final StringRedisTemplate redisTemplate;

    @Override
    public String publish(StreamMessageType type, String payload, Long outboxId) {
        Map<String, String> map = new HashMap<>();
        map.put("type", type.name());
        map.put("payload", payload);
        map.put("retry", "0");
        map.put("outboxId", String.valueOf(outboxId));

        StreamOperations<String, String, String> ops = redisTemplate.opsForStream();
        RecordId id = ops.add(MapRecord.create(streamKey, map));

        return id != null ? id.getValue() : null;
    }

    @Override
    public List<String> publishBatch(List<Outbox> outboxList) {
        return redisTemplate.executePipelined(
                        (RedisCallback<Object>) connection -> {
                            for (Outbox outbox : outboxList) {
                                Map<byte[], byte[]> map = new HashMap<>();
                                map.put("type".getBytes(), outbox.getType().getBytes());
                                map.put("payload".getBytes(), outbox.getPayload().getBytes());
                                map.put("retry".getBytes(), "0".getBytes());
                                map.put("outboxId".getBytes(), outbox.getId().toString().getBytes());

                                // byte[]로 변환하여 add
                                MapRecord<byte[], byte[], byte[]> record = StreamRecords
                                        .rawBytes(map)
                                        .withStreamKey(streamKey.getBytes());

                                connection.streamCommands().xAdd(record);
                            }
                            return null;
                        })
                .stream().map(result -> {
                    if (result instanceof RecordId) {
                        return ((RecordId) result).getValue();
                    } else {
                        return null;
                    }
                })
                .toList();
    }
}
