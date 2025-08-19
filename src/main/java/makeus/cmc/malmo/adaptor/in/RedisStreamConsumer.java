package makeus.cmc.malmo.adaptor.in;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import makeus.cmc.malmo.adaptor.message.*;
import makeus.cmc.malmo.application.port.in.chat.ProcessMessageUseCase;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.connection.stream.*;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class RedisStreamConsumer {

    @Value("${spring.data.redis.stream-key}")
    private String streamKey;

    @Value("${spring.data.redis.consumer-group}")
    private String consumerGroup;

    private final RedisTemplate<String, Object> redisTemplate;
    private final ProcessMessageUseCase processMessageUseCase;
    private final ObjectMapper objectMapper;

    @PostConstruct
    public void init() {
        try {
            // Consumer Group 생성 (이미 존재하면 무시)
            try {
                redisTemplate.opsForStream().createGroup(streamKey, ReadOffset.from("0"), consumerGroup);
                log.info("Created consumer group: {}", consumerGroup);
            } catch (Exception e) {
                log.debug("Consumer group already exists or stream doesn't exist yet");
            }
        } catch (Exception e) {
            log.error("Error creating consumer group", e);
        }
    }

    public void onMessage(MapRecord<String, String, String> record) {
        try {
            String type = record.getValue().get("type");
            String payloadJson = record.getValue().get("payload");

            log.info("Processing record type={}, id={}", type, record.getId());

            JsonNode payloadNode = objectMapper.readTree(payloadJson);

            switch (StreamMessageType.valueOf(type)) {
                case REQUEST_CHAT_MESSAGE:
                    processChatMessage(payloadNode);
                    break;
                case REQUEST_SUMMARY:
                    processSummary(payloadNode);
                    break;
                case REQUEST_TOTAL_SUMMARY:
                    processTotalSummary(payloadNode);
                    break;
                case REQUEST_EXTRACT_METADATA:
                    processMetadata(payloadNode);
                    break;
                default:
                    log.warn("Unknown message type: {}", type);
            }

            // 메시지 처리 완료 → ACK
            redisTemplate.opsForStream().acknowledge(streamKey, consumerGroup, record.getId());

        } catch (Exception e) {
            log.error("Error processing record {}", record.getId(), e);
            handleFailedMessage(record);
        }
    }

    private void processChatMessage(JsonNode payloadNode) {
        processMessageUseCase.processStreamChatMessage(
                ProcessMessageUseCase.ProcessMessageCommand.builder()
                        .memberId(payloadNode.get("memberId").asLong())
                        .chatRoomId(payloadNode.get("chatRoomId").asLong())
                        .nowMessage(payloadNode.get("nowMessage").asText())
                        .promptLevel(payloadNode.get("promptLevel").asInt())
                        .build()
        );
    }

    private void processSummary(JsonNode payloadNode) {
        processMessageUseCase.processSummary(
                ProcessMessageUseCase.ProcessSummaryCommand.builder()
                        .chatRoomId(payloadNode.get("chatRoomId").asLong())
                        .promptLevel(payloadNode.get("promptLevel").asInt())
                        .build()
        );
    }

    private void processTotalSummary(JsonNode payloadNode) {
        processMessageUseCase.processTotalSummary(
                ProcessMessageUseCase.ProcessTotalSummaryCommand.builder()
                        .chatRoomId(payloadNode.get("chatRoomId").asLong())
                        .build()
        );
    }

    private void processMetadata(JsonNode payloadNode) {
        processMessageUseCase.processAnswerMetadata(
                ProcessMessageUseCase.ProcessAnswerCommand.builder()
                        .coupleId(payloadNode.get("coupleId").asLong())
                        .memberId(payloadNode.get("memberId").asLong())
                        .coupleQuestionId(payloadNode.get("coupleQuestionId").asLong())
                        .build()
        );
    }

    private void handleFailedMessage(MapRecord<String, String, String> record) {
        try {
            // 현재 retry 횟수 확인
            Object retryCountObj = record.getValue().get("retry");
            int retryCount = retryCountObj != null ? Integer.parseInt(String.valueOf(retryCountObj)) : 0;

            // 최대 재시도 횟수 설정 (예: 3회)
            int maxRetries = 3;

            if (retryCount < maxRetries) {
                // retry count 증가
                retryCount++;

                // 새로운 메시지 생성 (기존 데이터 + retry count 업데이트)
                ObjectRecord<String, Map<String, String>> retryRecord = StreamRecords.objectBacked(record.getValue())
                        .withStreamKey(streamKey);

                // retryCount 필드 추가/업데이트
                retryRecord.getValue().put("type", record.getValue().get("type"));
                retryRecord.getValue().put("payload", record.getValue().get("payload"));
                retryRecord.getValue().put("retry", String.valueOf(retryCount));
                retryRecord.getValue().put("originalId", record.getId().getValue());

                // Stream에 다시 publish
                redisTemplate.opsForStream().add(retryRecord);

                log.info("Retry message published - originalId: {}, retryCount: {}",
                        record.getId(), retryCount);
            } else {
                log.error("Maximum retry count exceeded for message: {}", record.getId());
                // DLQ에 실패한 메시지 추가
                String dlqKey = streamKey + ":dlq";
                ObjectRecord<String, Map<String, String>> dlqRecord = StreamRecords.objectBacked(record.getValue())
                        .withStreamKey(dlqKey);
                dlqRecord.getValue().put("failedAt", String.valueOf(System.currentTimeMillis()));
                dlqRecord.getValue().put("originalId", record.getId().getValue());

                redisTemplate.opsForStream().add(dlqRecord);
            }
        } catch (Exception e) {
            log.error("Error handling failed message: {}", record.getId(), e);
        }
    }
}
