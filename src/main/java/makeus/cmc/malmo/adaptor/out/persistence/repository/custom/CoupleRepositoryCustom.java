package makeus.cmc.malmo.adaptor.out.persistence.repository.custom;

import makeus.cmc.malmo.adaptor.out.persistence.entity.couple.CoupleEntity;

import java.util.Optional;

public interface CoupleRepositoryCustom {
    Optional<CoupleEntity> findCoupleByMemberId(Long memberId);

    Optional<CoupleEntity> findCoupleByMemberIdAndPartnerId(Long memberId, Long partnerId);
}
