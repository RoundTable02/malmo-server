package makeus.cmc.malmo.adaptor.out.persistence.mapper;

import makeus.cmc.malmo.adaptor.out.persistence.entity.couple.CoupleEntity;
import makeus.cmc.malmo.adaptor.out.persistence.entity.couple.CoupleMemberEntity;
import makeus.cmc.malmo.adaptor.out.persistence.entity.couple.CoupleMemberStateJpa;
import makeus.cmc.malmo.adaptor.out.persistence.entity.couple.CoupleStateJpa;
import makeus.cmc.malmo.adaptor.out.persistence.entity.value.CoupleEntityId;
import makeus.cmc.malmo.adaptor.out.persistence.entity.value.MemberEntityId;
import makeus.cmc.malmo.domain.model.couple.Couple;
import makeus.cmc.malmo.domain.model.couple.CoupleMember;
import makeus.cmc.malmo.domain.model.couple.CoupleMemberState;
import makeus.cmc.malmo.domain.model.couple.CoupleState;
import makeus.cmc.malmo.domain.model.value.CoupleId;
import makeus.cmc.malmo.domain.model.value.MemberId;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CoupleAggregateMapper {

    // Couple
    public Couple toDomain(CoupleEntity entity) {
        List<CoupleMember> members = entity.getCoupleMembers().stream()
                .map(member -> toDomain(member, CoupleId.of(entity.getId())))
                .toList();

        return Couple.builder()
                .id(entity.getId())
                .startLoveDate(entity.getStartLoveDate())
                .coupleState(toCoupleState(entity.getCoupleStateJpa()))
                .coupleMembers(members)
                .createdAt(entity.getCreatedAt())
                .modifiedAt(entity.getModifiedAt())
                .deletedAt(entity.getDeletedAt())
                .build();
    }

    public CoupleEntity toEntity(Couple domain) {
        List<CoupleMemberEntity> coupleMembers = domain.getCoupleMembers().stream()
                .map(member -> toEntity(member, domain.getId()))
                .toList();

        return CoupleEntity.builder()
                .id(domain.getId())
                .startLoveDate(domain.getStartLoveDate())
                .coupleStateJpa(toCoupleStateJpa(domain.getCoupleState()))
                .coupleMembers(coupleMembers)
                .createdAt(domain.getCreatedAt())
                .modifiedAt(domain.getModifiedAt())
                .deletedAt(domain.getDeletedAt())
                .build();
    }

    // CoupleMember
    private CoupleMember toDomain(CoupleMemberEntity entity, CoupleId coupleId) {
        return CoupleMember.builder()
                .id(entity.getId())
                .memberId(MemberId.of(entity.getMemberEntityId().getValue()))
                .coupleId(coupleId)
                .coupleMemberState(toCoupleMemberState(entity.getCoupleMemberStateJpa()))
                .createdAt(entity.getCreatedAt())
                .modifiedAt(entity.getModifiedAt())
                .deletedAt(entity.getDeletedAt())
                .build();
    }

    private CoupleMemberEntity toEntity(CoupleMember domain, Long coupleId) {
        return CoupleMemberEntity.builder()
                .id(domain.getId())
                .memberEntityId(MemberEntityId.of(domain.getMemberId().getValue()))
                .coupleEntityId(CoupleEntityId.of(coupleId))
                .coupleMemberStateJpa(toCoupleMemberStateJpa(domain.getCoupleMemberState()))
                .createdAt(domain.getCreatedAt())
                .modifiedAt(domain.getModifiedAt())
                .deletedAt(domain.getDeletedAt())
                .build();
    }

    // enum
    private CoupleState toCoupleState(CoupleStateJpa entity) {
        return entity != null ? CoupleState.valueOf(entity.name()) : null;
    }

    private CoupleStateJpa toCoupleStateJpa(CoupleState domain) {
        return domain != null ? CoupleStateJpa.valueOf(domain.name()) : null;
    }

    private CoupleMemberState toCoupleMemberState(CoupleMemberStateJpa entity) {
        return entity != null ? CoupleMemberState.valueOf(entity.name()) : null;
    }

    private CoupleMemberStateJpa toCoupleMemberStateJpa(CoupleMemberState domain) {
        return domain != null ? CoupleMemberStateJpa.valueOf(domain.name()) : null;
    }
}



