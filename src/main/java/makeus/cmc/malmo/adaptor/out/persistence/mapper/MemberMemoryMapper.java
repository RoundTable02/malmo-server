package makeus.cmc.malmo.adaptor.out.persistence.mapper;

import makeus.cmc.malmo.adaptor.out.persistence.entity.member.MemberMemoryEntity;
import makeus.cmc.malmo.adaptor.out.persistence.entity.value.CoupleMemberEntityId;
import makeus.cmc.malmo.adaptor.out.persistence.entity.value.MemberEntityId;
import makeus.cmc.malmo.domain.model.member.MemberMemory;
import makeus.cmc.malmo.domain.value.id.CoupleMemberId;
import makeus.cmc.malmo.domain.value.id.MemberId;
import org.springframework.stereotype.Component;

@Component
public class MemberMemoryMapper {

    public MemberMemory toDomain(MemberMemoryEntity entity) {
        if (entity == null) {
            return null;
        }

        return MemberMemory.from(
                entity.getId(),
                entity.getCoupleMemberEntityId() != null ? CoupleMemberId.of(entity.getCoupleMemberEntityId().getValue()) : null,
                entity.getContent(),
                entity.getMemberMemoryState(),
                entity.getCreatedAt(),
                entity.getModifiedAt(),
                entity.getDeletedAt()
        );
    }

    public MemberMemoryEntity toEntity(MemberMemory memberMemory) {
        if (memberMemory == null) {
            return null;
        }

        return MemberMemoryEntity.builder()
                .id(memberMemory.getId())
                .coupleMemberEntityId(memberMemory.getCoupleMemberId() != null ? CoupleMemberEntityId.of(memberMemory.getCoupleMemberId().getValue()) : null)
                .content(memberMemory.getContent())
                .memberMemoryState(memberMemory.getMemberMemoryState())
                .createdAt(memberMemory.getCreatedAt())
                .modifiedAt(memberMemory.getModifiedAt())
                .deletedAt(memberMemory.getDeletedAt())
                .build();
    }


}
