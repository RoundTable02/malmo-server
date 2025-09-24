package makeus.cmc.malmo.adaptor.in;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import makeus.cmc.malmo.application.helper.outbox.OutboxMessageSavedEvent;
import makeus.cmc.malmo.application.port.in.PublishStreamMessageUseCase;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
@Slf4j
public class OutboxMessageSavedEventListener {

    private final PublishStreamMessageUseCase publishStreamMessageUseCase;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void handleOutboxEvent(OutboxMessageSavedEvent event) {
        publishStreamMessageUseCase.publish(event.getOutboxId());
    }
}
