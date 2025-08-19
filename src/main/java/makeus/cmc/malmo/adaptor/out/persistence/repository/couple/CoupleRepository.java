package makeus.cmc.malmo.adaptor.out.persistence.repository.couple;

import makeus.cmc.malmo.adaptor.out.persistence.entity.couple.CoupleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface CoupleRepository extends JpaRepository<CoupleEntity, Long>, CoupleRepositoryCustom {
}
