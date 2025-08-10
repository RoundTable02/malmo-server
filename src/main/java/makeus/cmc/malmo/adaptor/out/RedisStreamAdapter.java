package makeus.cmc.malmo.adaptor.out;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import makeus.cmc.malmo.adaptor.message.StreamMessage;
import makeus.cmc.malmo.adaptor.message.StreamMessageType;
import makeus.cmc.malmo.application.port.out.chat.PublishStreamMessagePort;
import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.connection.stream.RecordId;
import org.springframework.data.redis.core.StreamOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

import static makeus.cmc.malmo.util.GlobalConstants.PUBSUB_CHANNEL;
import static makeus.cmc.malmo.util.GlobalConstants.STREAM_KEY;

@Slf4j
@Service
@RequiredArgsConstructor
public class RedisStreamAdapter implements PublishStreamMessagePort {

    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;

    public void publish(StreamMessageType type, StreamMessage payload) {
        try {
            Map<String, String> map = new HashMap<>();
            map.put("type", type.name());
            map.put("payload", objectMapper.writeValueAsString(payload));
            map.put("retry", "0");

            StreamOperations<String, String, String> ops = redisTemplate.opsForStream();
            RecordId id = ops.add(MapRecord.create(STREAM_KEY, map));

            redisTemplate.convertAndSend(PUBSUB_CHANNEL, "new-message");

            log.info("Published message to Redis Stream: type={}, payload={}, id={}", type, payload, id);
        } catch (Exception e) {
            throw new RuntimeException("Failed to serialize payload", e);
        }
    }
}
