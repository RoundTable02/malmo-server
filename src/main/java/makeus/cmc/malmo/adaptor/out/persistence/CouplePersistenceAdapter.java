package makeus.cmc.malmo.adaptor.out.persistence;

import lombok.RequiredArgsConstructor;
import makeus.cmc.malmo.adaptor.out.persistence.entity.couple.CoupleEntity;
import makeus.cmc.malmo.adaptor.out.persistence.mapper.CoupleAggregateMapper;
import makeus.cmc.malmo.adaptor.out.persistence.repository.CoupleRepository;
import makeus.cmc.malmo.application.port.out.couple.LoadCouplePort;
import makeus.cmc.malmo.application.port.out.couple.SaveCouplePort;
import makeus.cmc.malmo.domain.model.couple.Couple;
import makeus.cmc.malmo.domain.value.id.CoupleId;
import makeus.cmc.malmo.domain.value.id.CoupleMemberId;
import makeus.cmc.malmo.domain.value.id.MemberId;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class CouplePersistenceAdapter implements SaveCouplePort, LoadCouplePort {

    private final CoupleRepository coupleRepository;
    private final CoupleAggregateMapper coupleAggregateMapper;

    @Override
    public Couple saveCouple(Couple couple) {
        CoupleEntity entity = coupleAggregateMapper.toEntity(couple);
        CoupleEntity savedCoupleEntity = coupleRepository.save(entity);
        return coupleAggregateMapper.toDomain(savedCoupleEntity);
    }

    @Override
    public CoupleId loadCoupleIdByMemberId(MemberId memberId) {
        return CoupleId.of(coupleRepository.findCoupleIdByMemberId(memberId.getValue()));
    }

    @Override
    public CoupleMemberId loadCoupleMemberIdByMemberId(MemberId memberId) {
        return CoupleMemberId.of(coupleRepository.findCoupleMemberIdByMemberId(memberId.getValue()));
    }

    @Override
    public Optional<Couple> loadCoupleByMemberId(MemberId memberId) {
        return coupleRepository.findCoupleByMemberId(memberId.getValue())
                .map(coupleAggregateMapper::toDomain);
    }

    @Override
    public Optional<Couple> loadCoupleByMemberIdAndPartnerId(MemberId memberId, MemberId partnerId) {
        return coupleRepository.findCoupleByMemberIdAndPartnerId(memberId.getValue(), partnerId.getValue())
                .map(coupleAggregateMapper::toDomain);
    }
}
