package makeus.cmc.malmo.adaptor.in;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import makeus.cmc.malmo.application.port.in.RetryPublishingUseCase;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class OutboxScheduler {

    private final RetryPublishingUseCase retryPublishingUseCase;

    @Scheduled(fixedRate = 3000) // PENDING 상태의 메시지를 재발행, 3초마다 실행
    public void processPendingMessages() {
        retryPublishingUseCase.retryPublishing();
    }

    @Scheduled(fixedRate = 300000) // FAILED 상태의 메시지를 재발행, 5분마다 실행
    public void processFailedMessages() {
        retryPublishingUseCase.retryFailedMessages();
    }

    @Scheduled(fixedRate = 600000) // PEL에서 ACK 되지 못한 메시지를 ACK 및 FAILED 처리, 10분마다 실행
    public void processPelMessages() {
        retryPublishingUseCase.retryPendingMessages();
    }
}
