package makeus.cmc.malmo.adaptor.in;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import makeus.cmc.malmo.application.port.in.RetryPublishingUseCase;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class OutboxScheduler {

    private final RetryPublishingUseCase retryPublishingUseCase;

    @Scheduled(fixedRate = 3000) // PENDING 상태의 메시지를 재발행, 3초마다 실행
    @Transactional
    public void processPendingMessages() {
        retryPublishingUseCase.retryPublishing();
    }

    @Scheduled(fixedRate = 60000) // FAILED 상태의 메시지를 재발행, 1분마다 실행
    @Transactional
    public void processFailedMessages() {
        retryPublishingUseCase.retryFailedMessages();
    }


}
