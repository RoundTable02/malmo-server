package makeus.cmc.malmo.adaptor.out.persistence.repository;

import makeus.cmc.malmo.adaptor.out.persistence.entity.OutboxEntity;
import makeus.cmc.malmo.domain.value.state.OutboxState;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface OutboxRepository extends JpaRepository<OutboxEntity, Long> {

    List<OutboxEntity> findByStateAndModifiedAtBefore(OutboxState state, LocalDateTime before);
}
