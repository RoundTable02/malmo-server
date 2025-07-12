package makeus.cmc.malmo.adaptor.out.persistence.mapper;

import makeus.cmc.malmo.adaptor.out.persistence.entity.couple.CoupleEntity;
import makeus.cmc.malmo.adaptor.out.persistence.entity.couple.CoupleMemberEntity;
import makeus.cmc.malmo.adaptor.out.persistence.entity.value.CoupleEntityId;
import makeus.cmc.malmo.adaptor.out.persistence.entity.value.MemberEntityId;
import makeus.cmc.malmo.domain.model.couple.Couple;
import makeus.cmc.malmo.domain.model.couple.CoupleMember;
import makeus.cmc.malmo.domain.value.id.CoupleId;
import makeus.cmc.malmo.domain.value.id.MemberId;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CoupleAggregateMapper {

    // Couple
    public Couple toDomain(CoupleEntity entity) {
        List<CoupleMember> members = entity.getCoupleMembers().stream()
                .map(member -> toDomain(member, CoupleId.of(entity.getId())))
                .toList();

        return Couple.from(
                entity.getId(),
                entity.getStartLoveDate(),
                entity.getCoupleState(),
                members,
                entity.getCreatedAt(),
                entity.getModifiedAt(),
                entity.getDeletedAt()
        );
    }

    public CoupleEntity toEntity(Couple domain) {
        List<CoupleMemberEntity> coupleMembers = domain.getCoupleMembers().stream()
                .map(member -> toEntity(member, domain.getId()))
                .toList();

        return CoupleEntity.builder()
                .id(domain.getId())
                .startLoveDate(domain.getStartLoveDate())
                .coupleState(domain.getCoupleState())
                .coupleMembers(coupleMembers)
                .createdAt(domain.getCreatedAt())
                .modifiedAt(domain.getModifiedAt())
                .deletedAt(domain.getDeletedAt())
                .build();
    }

    // CoupleMember
    private CoupleMember toDomain(CoupleMemberEntity entity, CoupleId coupleId) {
        return CoupleMember.from(
                entity.getId(),
                MemberId.of(entity.getMemberEntityId().getValue()),
                coupleId,
                entity.getCoupleMemberState(),
                entity.getCreatedAt(),
                entity.getModifiedAt(),
                entity.getDeletedAt()
        );
    }

    private CoupleMemberEntity toEntity(CoupleMember domain, Long coupleId) {
        return CoupleMemberEntity.builder()
                .id(domain.getId())
                .memberEntityId(MemberEntityId.of(domain.getMemberId().getValue()))
                .coupleEntityId(CoupleEntityId.of(coupleId))
                .coupleMemberState(domain.getCoupleMemberState())
                .createdAt(domain.getCreatedAt())
                .modifiedAt(domain.getModifiedAt())
                .deletedAt(domain.getDeletedAt())
                .build();
    }
}



