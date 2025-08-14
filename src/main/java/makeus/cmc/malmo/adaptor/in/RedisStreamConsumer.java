package makeus.cmc.malmo.adaptor.in;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import makeus.cmc.malmo.adaptor.message.*;
import makeus.cmc.malmo.application.port.in.chat.ProcessMessageUseCase;
import org.springframework.data.redis.connection.stream.*;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;
import java.util.Map;

import static makeus.cmc.malmo.util.GlobalConstants.PUBSUB_CHANNEL;
import static makeus.cmc.malmo.util.GlobalConstants.STREAM_KEY;

@Slf4j
@Service
@RequiredArgsConstructor
public class RedisStreamConsumer {

    private final RedisTemplate<String, Object> redisTemplate;

    private static final String CONSUMER_GROUP = "malmo-group";
    private static final String CONSUMER_NAME = "malmo-consumer";

    private final ProcessMessageUseCase processMessageUseCase;
    private final ObjectMapper objectMapper;

    @PostConstruct
    public void initialize() {
        processExistingRecords();
    }

    private void processExistingRecords() {
        try {
            // Consumer Group 생성 (이미 존재하면 무시)
            try {
                redisTemplate.opsForStream().createGroup(STREAM_KEY, ReadOffset.from("0"), CONSUMER_GROUP);
            } catch (Exception e) {
                log.debug("Consumer group already exists or stream doesn't exist yet");
            }

            // Pending 메시지 확인
            PendingMessagesSummary pendingMessages = redisTemplate.opsForStream()
                .pending(STREAM_KEY, CONSUMER_GROUP);

            if (pendingMessages != null && pendingMessages.getTotalPendingMessages() > 0) {
                processStreamRecords();
            } else {
                log.info("No pending messages in stream: {}", STREAM_KEY);
            }
        } catch (Exception e) {
            log.error("Error processing existing records", e);
        }
    }

    // 2. Pub/Sub 이벤트 감지 시 호출되는 메서드
    public void onPubSubMessage(String message) {
        log.info("Received pub/sub notification: {}", message);
        // Stream에서 새로운 레코드 처리
        processStreamRecords();
    }

    private void processStreamRecords() {
        try {
            // Stream에서 레코드 읽기
            List<MapRecord<String, Object, Object>> records = redisTemplate.opsForStream()
                .read(Consumer.from(CONSUMER_GROUP, CONSUMER_NAME),
                      StreamReadOptions.empty().count(10).block(Duration.ofSeconds(1)),
                      StreamOffset.create(STREAM_KEY, ReadOffset.lastConsumed()));

            for (MapRecord<String, Object, Object> record : records) {
                processRecord(record);
                // 메시지 ACK 처리
                redisTemplate.opsForStream().acknowledge(STREAM_KEY, CONSUMER_GROUP, record.getId());
            }

        } catch (Exception e) {
            log.error("Error processing stream records", e);
        }
    }

    // 3. 레코드 타입에 따른 처리
    private void processRecord(MapRecord<String, Object, Object> record) {
        try {
            String type = (String) record.getValue().get("type");
            log.info("Processing record with type: {}, id: {}", type, record.getId());
            log.info("Processing user event: {}", record.getValue());

            Map<Object, Object> value = record.getValue();
            String payloadJson = (String) value.get("payload");

            // JSON 파싱
            JsonNode payloadNode = objectMapper.readTree(payloadJson);

            switch (StreamMessageType.valueOf(type)) {
                case REQUEST_CHAT_MESSAGE:
                    processStreamChatMessageEvent(payloadNode);
                    break;
                case REQUEST_SUMMARY:
                    processRequestSummaryEvent(payloadNode);
                    break;
                case REQUEST_TOTAL_SUMMARY:
                    processRequestTotalSummaryEvent(payloadNode);
                    break;
                case REQUEST_EXTRACT_METADATA:
                    processRequestMetadataEvent(payloadNode);
                    break;
                default:
                    log.warn("Unknown message type: {}", type);
            }

        } catch (Exception e) {
            log.error("Error processing record: {}", record.getId(), e);
            // 실패한 메시지를 retry + 1하고 다시 stream에 publish
            handleFailedMessage(record);
        }
    }

    private void processStreamChatMessageEvent(JsonNode payloadNode) {
        // Redis Stream 레코드를 StreamChatMessage로 변환
        StreamChatMessage streamChatMessage = new StreamChatMessage(
                payloadNode.get("memberId").asLong(),
                payloadNode.get("chatRoomId").asLong(),
                payloadNode.get("nowMessage").asText(),
                payloadNode.get("promptLevel").asInt()
        );

        processMessageUseCase.processStreamChatMessage(
                ProcessMessageUseCase.ProcessMessageCommand.builder()
                        .memberId(streamChatMessage.getMemberId())
                        .chatRoomId(streamChatMessage.getChatRoomId())
                        .nowMessage(streamChatMessage.getNowMessage())
                        .promptLevel(streamChatMessage.getPromptLevel())
                        .build()
        );

    }

    private void processRequestSummaryEvent(JsonNode payloadNode) {
        RequestSummaryMessage requestSummaryMessage = new RequestSummaryMessage(
                payloadNode.get("chatRoomId").asLong(),
                payloadNode.get("promptLevel").asInt()
        );

        processMessageUseCase.processSummary(
                ProcessMessageUseCase.ProcessSummaryCommand.builder()
                        .chatRoomId(requestSummaryMessage.getChatRoomId())
                        .promptLevel(requestSummaryMessage.getPromptLevel())
                        .build()
        );
    }

    private void processRequestTotalSummaryEvent(JsonNode payloadNode) {
        RequestTotalSummaryMessage requestTotalSummaryMessage = new RequestTotalSummaryMessage(
                payloadNode.get("chatRoomId").asLong()
        );

        processMessageUseCase.processTotalSummary(
                ProcessMessageUseCase.ProcessTotalSummaryCommand.builder()
                        .chatRoomId(requestTotalSummaryMessage.getChatRoomId())
                        .build()
        );
    }

    private void processRequestMetadataEvent(JsonNode payloadNode) {
        RequestExtractMetadataMessage requestExtractMetadataMessage = new RequestExtractMetadataMessage(
                payloadNode.get("coupleQuestionId").asLong(),
                payloadNode.get("coupleMemberId").asLong()
        );

        processMessageUseCase.processAnswerMetadata(
                ProcessMessageUseCase.ProcessAnswerCommand.builder()
                        .coupleQuestionId(requestExtractMetadataMessage.getCoupleQuestionId())
                        .coupleMemberId(requestExtractMetadataMessage.getCoupleMemberId())
                        .build()
        );
    }

    private void handleFailedMessage(MapRecord<String, Object, Object> record) {
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
                ObjectRecord<String, Map<Object, Object>> retryRecord = StreamRecords.objectBacked(record.getValue())
                    .withStreamKey(STREAM_KEY);

                // retryCount 필드 추가/업데이트
                retryRecord.getValue().put("type", record.getValue().get("type"));
                retryRecord.getValue().put("payload", record.getValue().get("payload"));
                retryRecord.getValue().put("retry", String.valueOf(retryCount));
                retryRecord.getValue().put("originalId", record.getId().getValue());

                // Stream에 다시 publish
                redisTemplate.opsForStream().add(retryRecord);

                log.info("Retry message published - originalId: {}, retryCount: {}",
                    record.getId(), retryCount);

                // Pub/Sub으로 알림 전송
                redisTemplate.convertAndSend(PUBSUB_CHANNEL, "retry_message");
            } else {
                log.error("Maximum retry count exceeded for message: {}", record.getId());
                // DLQ에 실패한 메시지 추가
                String dlqKey = STREAM_KEY + ":dlq";
                ObjectRecord<String, Map<Object, Object>> dlqRecord = StreamRecords.objectBacked(record.getValue())
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

