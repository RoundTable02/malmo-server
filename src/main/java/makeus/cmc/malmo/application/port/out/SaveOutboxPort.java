package makeus.cmc.malmo.application.port.out;

import makeus.cmc.malmo.domain.model.Outbox;

import java.util.List;

public interface SaveOutboxPort {
    Outbox save(Outbox outbox);
    void saveAll(List<Outbox> outboxList);
}
