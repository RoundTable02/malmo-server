package makeus.cmc.malmo.adaptor.out.persistence.mapper;

import makeus.cmc.malmo.adaptor.out.persistence.entity.couple.CoupleEntity;
import makeus.cmc.malmo.adaptor.out.persistence.entity.couple.CoupleMemberSnapshotEntity;
import makeus.cmc.malmo.adaptor.out.persistence.entity.value.MemberEntityId;
import makeus.cmc.malmo.domain.model.couple.Couple;
import makeus.cmc.malmo.domain.model.couple.CoupleMemberSnapshot;
import makeus.cmc.malmo.domain.value.id.MemberId;
import org.springframework.stereotype.Component;

@Component
public class CoupleAggregateMapper {

    // Couple
    public Couple toDomain(CoupleEntity entity) {
        if (entity == null) {
            return null;
        }
        CoupleMemberSnapshot firstSnapshot = toDomain(entity.getFirstMemberSnapshot());
        CoupleMemberSnapshot secondSnapshot = toDomain(entity.getSecondMemberSnapshot());

        return Couple.from(
                entity.getId(),
                entity.getStartLoveDate(),
                entity.getFirstMemberId() != null ? MemberId.of(entity.getFirstMemberId().getValue()) : null,
                entity.getSecondMemberId() != null ? MemberId.of(entity.getSecondMemberId().getValue()) : null,
                entity.getCoupleState(),
                firstSnapshot,
                secondSnapshot,
                entity.getCreatedAt(),
                entity.getModifiedAt(),
                entity.getDeletedAt(),
                entity.getIsStartLoveDateUpdated()
        );
    }

    public CoupleEntity toEntity(Couple domain) {
        if (domain == null) {
            return null;
        }
        CoupleMemberSnapshotEntity firstSnapshotEntity = toEntity(domain.getFirstMemberSnapshot());
        CoupleMemberSnapshotEntity secondSnapshotEntity = toEntity(domain.getSecondMemberSnapshot());

        return CoupleEntity.builder()
                .id(domain.getId())
                .startLoveDate(domain.getStartLoveDate())
                .coupleState(domain.getCoupleState())
                .isStartLoveDateUpdated(domain.getIsStartLoveDateUpdated())
                .firstMemberId(domain.getFirstMemberId() != null ? MemberEntityId.of(domain.getFirstMemberId().getValue()) : null)
                .secondMemberId(domain.getSecondMemberId() != null ? MemberEntityId.of(domain.getSecondMemberId().getValue()) : null)
                .firstMemberSnapshot(firstSnapshotEntity)
                .secondMemberSnapshot(secondSnapshotEntity)
                .createdAt(domain.getCreatedAt())
                .modifiedAt(domain.getModifiedAt())
                .deletedAt(domain.getDeletedAt())
                .build();
    }

    private CoupleMemberSnapshotEntity toEntity(CoupleMemberSnapshot domain) {
        if (domain == null) {
            return null;
        }
        return CoupleMemberSnapshotEntity.builder()
                .nickname(domain.getNickname())
                .loveTypeCategory(domain.getLoveTypeCategory())
                .avoidanceRate(domain.getAvoidanceRate())
                .anxietyRate(domain.getAnxietyRate())
                .build();
    }

    private CoupleMemberSnapshot toDomain(CoupleMemberSnapshotEntity entity) {
        if (entity == null) {
            return null;
        }
        return CoupleMemberSnapshot.from(
                entity.getNickname(),
                entity.getLoveTypeCategory(),
                entity.getAvoidanceRate(),
                entity.getAnxietyRate()
        );
    }
}



