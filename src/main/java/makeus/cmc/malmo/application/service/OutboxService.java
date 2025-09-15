package makeus.cmc.malmo.application.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import makeus.cmc.malmo.adaptor.message.StreamMessageType;
import makeus.cmc.malmo.application.port.in.CompleteOutboxUseCase;
import makeus.cmc.malmo.application.port.in.PublishStreamMessageUseCase;
import makeus.cmc.malmo.application.port.in.RetryPublishingUseCase;
import makeus.cmc.malmo.application.port.out.CheckOpenAIHealth;
import makeus.cmc.malmo.application.port.out.LoadOutboxPort;
import makeus.cmc.malmo.application.port.out.SaveOutboxPort;
import makeus.cmc.malmo.application.port.out.chat.PublishStreamMessagePort;
import makeus.cmc.malmo.domain.model.Outbox;
import makeus.cmc.malmo.domain.value.state.OutboxState;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class OutboxService implements PublishStreamMessageUseCase, RetryPublishingUseCase, CompleteOutboxUseCase {


    private final PublishStreamMessagePort publishStreamMessagePort;
    private final LoadOutboxPort loadOutboxPort;
    private final SaveOutboxPort saveOutboxPort;
    private final CheckOpenAIHealth checkOpenAIHealth;

    @Override
    public void publish(Long outboxId) {
        log.debug("Handling event for outboxId: {}", outboxId);

        // Optional로 처리하여 스케줄러가 처리한 경우를 대비 (중복 메시지 방지)
        loadOutboxPort.findById(outboxId)
                .ifPresent(outbox -> {
                    // PENDING 상태일 때만 처리
                    if (!outbox.isPending()) {
                        log.info("Outbox id={} is not in PENDING state. Skipping.", outbox.getId());
                        return;
                    }

                    try {
                        String recordId = publishStreamMessagePort.publish(
                                StreamMessageType.valueOf(outbox.getType()),
                                outbox.getPayload(),
                                outbox.getId());

                        outbox.markAsSent(recordId);
                        // Redis 발행 성공 시 상태 업데이트까지 같은 트랜잭션
                        saveOutboxPort.save(outbox);
                        log.info("Published message from Outbox event: outboxId={}, id={}", outbox.getId(), recordId);
                    } catch (Exception e) {
                        log.error("Failed to publish message from Outbox event for id={}. Scheduler will retry.", outbox.getId(), e);
                        // 실패 시 아무것도 하지 않고 상태는 PENDING 유지
                        // 스케줄러가 나중에 이 메시지를 처리
                    }
                });
    }

    @Override
    public void retryPublishing() {
        // PENDING 상태인 메시지 중 5초 이상 지난 메시지를 재시도 처리
        List<Outbox> outboxList = loadOutboxPort.findByStateAndModifiedAtBefore(
                OutboxState.PENDING,
                LocalDateTime.now().minusSeconds(5));

        if (outboxList == null || outboxList.isEmpty()) {
            return;
        }

        // Redis Pipelining을 사용하여 모든 메시지를 한 번의 네트워크 요청으로 발행
        List<String> messageIds = publishStreamMessagePort.publishBatch(outboxList);

        // 파이프라인 실행 결과를 바탕으로 각 메시지의 상태를 업데이트
        for (int i = 0; i < outboxList.size(); i++) {
            Outbox outbox = outboxList.get(i);
            String messageId = messageIds.get(i);

            if (messageId != null) {
                // 발행 성공
                outbox.markAsSent(messageId);
                log.info("Re-published message to Redis Stream: outboxId={}, messageId={}", outbox.getId(), messageId);
            } else {
                // 발행 실패
                // retryCount 증가 및 최대 재시도 횟수 도달 시 FAILED로 상태 변경
                log.error("Failed to re-publish message from Outbox messageId={}", outbox.getId());

                if (outbox.getRetryCount() >= 3) {
                    log.error("Outbox messageId={} reached max retry limit. Marking as FAILED.", outbox.getId());
                    outbox.markAsFailed();
                } else {
                    outbox.incrementRetryCount();
                }
            }
        }

        // 변경된 모든 Outbox 상태를 단일 트랜잭션으로 DB에 저장
        saveOutboxPort.saveAll(outboxList);
    }

    @Override
    public void retryFailedMessages() {
        // OpenAI Health Check
        boolean isUp = checkOpenAIHealth.checkHealth();
        if (isUp) {
            // OpenAI가 정상 상태일 때
            // FAILED 상태인 메시지를 재시도 처리
            List<Outbox> failedOutboxList = loadOutboxPort.findByStateAndModifiedAtBefore(
                    OutboxState.FAILED,
                    LocalDateTime.now().minusMinutes(5));

            if (failedOutboxList == null || failedOutboxList.isEmpty()) {
                return;
            }

            publishStreamMessagePort.publishBatch(failedOutboxList);
        }
    }

    @Override
    public void completeOutbox(Long outboxId) {
        // 메시지 처리가 완료된 경우 DONE 상태로 업데이트
        loadOutboxPort.findById(outboxId)
                .ifPresent(outbox -> {
                    outbox.markAsDone();
                    saveOutboxPort.save(outbox);
                    log.info("Marked Outbox message as DONE: outboxId={}", outbox.getId());
                });
    }
}
