package makeus.cmc.malmo.adaptor.out;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import makeus.cmc.malmo.adaptor.message.StreamMessageType;
import makeus.cmc.malmo.application.port.out.chat.LoadPendingMessagePort;
import makeus.cmc.malmo.domain.model.Outbox;
import makeus.cmc.malmo.application.port.out.chat.PublishStreamMessagePort;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Range;
import org.springframework.data.redis.connection.stream.*;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StreamOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.springframework.data.redis.connection.RedisStreamCommands.*;


@Slf4j
@Service
@RequiredArgsConstructor
public class RedisStreamAdapter implements PublishStreamMessagePort, LoadPendingMessagePort {

    @Value("${spring.data.redis.stream-key}")
    private String streamKey;

    @Value("${spring.data.redis.consumer-group}")
    private String consumerGroup;

    private static final String CONSUMER = "malmo-consumer-x";

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

    @Override
    public void acknowledge(String messageId) {
        redisTemplate.opsForStream().acknowledge(streamKey, consumerGroup, messageId);
    }

    @Override
    public List<PendingMessageDto> loadPendingMessages(Duration minIdleTime) {
        // 현재 컨슈머의 Pending 메시지 목록을 조회
        PendingMessagesSummary pendingMessagesSummary =
                redisTemplate.opsForStream().pending(streamKey, consumerGroup);

        if (pendingMessagesSummary == null || pendingMessagesSummary.getTotalPendingMessages() == 0) {
            return new ArrayList<>();
        }

        List<MapRecord<String, Object, Object>> allClaimedMessages = new ArrayList<>();

        pendingMessagesSummary.getPendingMessagesPerConsumer().forEach((staleConsumerName, messageCount) -> {
            // 해당 컨슈머의 Pending 메시지 상세 목록을 조회
            if (messageCount > 0) {
                // 해당 컨슈머의 Pending 메시지 상세 목록을 조회
                PendingMessages pendingMessages = redisTemplate.opsForStream().pending(
                        streamKey,
                        Consumer.from(consumerGroup, staleConsumerName),
                        Range.unbounded(),
                        messageCount);

                // Claim할 메시지 ID만 필터링하여 수집
                List<RecordId> messageIdsToClaim = pendingMessages.stream()
                        .filter(pending ->
                                pending.getElapsedTimeSinceLastDelivery().compareTo(minIdleTime) > 0)
                        .map(PendingMessage::getId)
                        .toList();

                if (!messageIdsToClaim.isEmpty()) {
                    // 수집된 ID들을 사용하여 현재 claimer가 메시지 소유하도록 Claim 수행
                    List<MapRecord<String, Object, Object>> claimed = redisTemplate.opsForStream().claim(
                            streamKey,
                            consumerGroup,
                            CONSUMER, // 메시지를 가져오는 새로운 소유자
                            XClaimOptions.minIdle(minIdleTime).ids(messageIdsToClaim.toArray(new RecordId[0]))
                    );
                    allClaimedMessages.addAll(claimed);
                }
            }
        });

        return allClaimedMessages.stream()
                .map(this::mapToPendingMessage)
                .collect(Collectors.toList());
    }

    private PendingMessageDto mapToPendingMessage(MapRecord<String, Object, Object> record) {
        Map<Object, Object> payload = record.getValue();
        return PendingMessageDto.builder()
                .messageId(record.getId().getValue())
                .outboxId(Long.valueOf(payload.get("outboxId").toString()))
                .payload(record.getValue())
                .build();
    }
}
