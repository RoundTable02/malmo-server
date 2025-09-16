package makeus.cmc.malmo.application.port.out;

import makeus.cmc.malmo.domain.model.Outbox;
import makeus.cmc.malmo.domain.value.state.OutboxState;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface LoadOutboxPort {
    List<Outbox> findByStateAndModifiedAtBefore(OutboxState state, LocalDateTime before);
    List<Outbox> findByState(OutboxState state);
    Optional<Outbox> findById(Long outboxId);
}
