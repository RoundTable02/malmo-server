package makeus.cmc.malmo.adaptor.out.persistence.adapter;

import lombok.RequiredArgsConstructor;
import makeus.cmc.malmo.adaptor.out.persistence.entity.OutboxEntity;
import makeus.cmc.malmo.adaptor.out.persistence.mapper.OutboxMapper;
import makeus.cmc.malmo.adaptor.out.persistence.repository.OutboxRepository;
import makeus.cmc.malmo.application.port.out.LoadOutboxPort;
import makeus.cmc.malmo.application.port.out.SaveOutboxPort;
import makeus.cmc.malmo.domain.model.Outbox;
import makeus.cmc.malmo.domain.value.state.OutboxState;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class OutboxPersistenceAdapter implements LoadOutboxPort, SaveOutboxPort {

    private final OutboxRepository outboxRepository;
    private final OutboxMapper outboxMapper;

    @Override
    public List<Outbox> findByStateAndModifiedAtBefore(OutboxState state, LocalDateTime before) {
        List<OutboxEntity> outboxList = outboxRepository.findByStateAndModifiedAtBefore(OutboxState.PENDING, before);
        return outboxList.stream()
                .map(outboxMapper::toDomain)
                .toList();
    }

    @Override
    public List<Outbox> findByState(OutboxState state) {
        return outboxRepository.findByState(state).stream()
                .map(outboxMapper::toDomain)
                .toList();
    }

    @Override
    public Optional<Outbox> findById(Long outboxId) {
        return outboxRepository.findById(outboxId)
                .map(outboxMapper::toDomain);
    }

    @Override
    public Outbox save(Outbox outbox) {
        OutboxEntity entity = outboxMapper.toEntity(outbox);
        OutboxEntity savedEntity = outboxRepository.save(entity);
        return outboxMapper.toDomain(savedEntity);
    }

    @Override
    public void saveAll(List<Outbox> outboxList) {
        List<OutboxEntity> entities = outboxList.stream()
                .map(outboxMapper::toEntity)
                .toList();
        outboxRepository.saveAll(entities);
    }
}
