package makeus.cmc.malmo.application.helper.outbox;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class OutboxMessageSavedEvent extends ApplicationEvent {
    private final Long outboxId;

    public OutboxMessageSavedEvent(Object source, Long outboxId) {
        super(source);
        this.outboxId = outboxId;
    }
}
