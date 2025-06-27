package makeus.cmc.malmo.adaptor.out.persistence.mapper;

import makeus.cmc.malmo.adaptor.out.persistence.entity.couple.CoupleEntity;
import makeus.cmc.malmo.adaptor.out.persistence.entity.couple.CoupleStateJpa;
import makeus.cmc.malmo.domain.model.couple.Couple;
import makeus.cmc.malmo.domain.model.couple.CoupleState;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class CoupleMapper {

    public Couple toDomain(CoupleEntity entity) {
        return Couple.builder()
                .id(entity.getId())
                .inviteCode(entity.getInviteCode())
                .startLoveDate(entity.getStartLoveDate())
                .coupleState(toCoupleState(entity.getCoupleStateJpa()))
                .deletedDate(entity.getDeletedDate())
                .createdAt(entity.getCreatedAt())
                .modifiedAt(entity.getModifiedAt())
                .deletedAt(entity.getDeletedAt())
                .build();
    }

    public CoupleEntity toEntity(Couple domain) {
        return CoupleEntity.builder()
                .id(domain.getId())
                .inviteCode(domain.getInviteCode())
                .startLoveDate(domain.getStartLoveDate())
                .coupleStateJpa(toCoupleStateJpa(domain.getCoupleState()))
                .deletedDate(domain.getDeletedDate())
                .build();
    }

    private CoupleState toCoupleState(CoupleStateJpa coupleStateJpa) {
        return Optional.ofNullable(coupleStateJpa)
                .map(cs -> CoupleState.valueOf(cs.name()))
                .orElse(null);
    }

    private CoupleStateJpa toCoupleStateJpa(CoupleState coupleState) {
        return Optional.ofNullable(coupleState)
                .map(cs -> CoupleStateJpa.valueOf(cs.name()))
                .orElse(null);
    }
}