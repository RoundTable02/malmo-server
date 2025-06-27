package makeus.cmc.malmo.adaptor.out.persistence.mapper;

import makeus.cmc.malmo.adaptor.out.persistence.entity.member.MemberMemoryEntity;
import makeus.cmc.malmo.domain.model.member.MemberMemory;
import org.springframework.stereotype.Component;

@Component
public class MemberMemoryMapper {

    private final MemberMapper memberMapper;

    public MemberMemoryMapper(MemberMapper memberMapper) {
        this.memberMapper = memberMapper;
    }

    public MemberMemory toDomain(MemberMemoryEntity entity) {
        return MemberMemory.builder()
                .id(entity.getId())
                .member(memberMapper.toDomain(entity.getMember()))
                .content(entity.getContent())
                .createdAt(entity.getCreatedAt())
                .modifiedAt(entity.getModifiedAt())
                .deletedAt(entity.getDeletedAt())
                .build();
    }

    public MemberMemoryEntity toEntity(MemberMemory domain) {
        return MemberMemoryEntity.builder()
                .id(domain.getId())
                .member(memberMapper.toEntity(domain.getMember()))
                .content(domain.getContent())
                .build();
    }
}