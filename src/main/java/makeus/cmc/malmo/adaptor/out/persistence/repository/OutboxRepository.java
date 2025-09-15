package makeus.cmc.malmo.adaptor.out.persistence.repository;

import makeus.cmc.malmo.adaptor.out.persistence.entity.OutboxEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OutboxRepository extends JpaRepository<OutboxEntity, Long> {
}
