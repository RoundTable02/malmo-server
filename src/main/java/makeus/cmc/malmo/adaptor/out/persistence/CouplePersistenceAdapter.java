package makeus.cmc.malmo.adaptor.out.persistence;

import lombok.RequiredArgsConstructor;
import makeus.cmc.malmo.adaptor.out.persistence.entity.couple.CoupleEntity;
import makeus.cmc.malmo.adaptor.out.persistence.mapper.CoupleAggregateMapper;
import makeus.cmc.malmo.adaptor.out.persistence.repository.CoupleRepository;
import makeus.cmc.malmo.application.port.out.SaveCouplePort;
import makeus.cmc.malmo.domain.model.couple.Couple;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CouplePersistenceAdapter implements SaveCouplePort {

    private final CoupleRepository coupleRepository;
    private final CoupleAggregateMapper coupleAggregateMapper;

    @Override
    public Couple saveCouple(Couple couple) {
        CoupleEntity entity = coupleAggregateMapper.toEntity(couple);
        CoupleEntity savedCoupleEntity = coupleRepository.save(entity);
        return coupleAggregateMapper.toDomain(savedCoupleEntity);
    }
}
