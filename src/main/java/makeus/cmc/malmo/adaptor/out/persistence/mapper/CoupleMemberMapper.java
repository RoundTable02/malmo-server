package makeus.cmc.malmo.adaptor.out.persistence.mapper;

import makeus.cmc.malmo.adaptor.out.persistence.entity.couple.CoupleMemberEntity;
import makeus.cmc.malmo.adaptor.out.persistence.entity.couple.CoupleMemberStateJpa;
import makeus.cmc.malmo.domain.model.couple.CoupleMember;
import makeus.cmc.malmo.domain.model.couple.CoupleMemberState;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class CoupleMemberMapper {

    private final MemberMapper memberMapper;
    private final CoupleMapper coupleMapper;

    public CoupleMemberMapper(MemberMapper memberMapper, CoupleMapper coupleMapper) {
        this.memberMapper = memberMapper;
        this.coupleMapper = coupleMapper;
    }

    public CoupleMember toDomain(CoupleMemberEntity entity) {
        return CoupleMember.builder()
                .id(entity.getId())
                .member(memberMapper.toDomain(entity.getMember()))
                .couple(coupleMapper.toDomain(entity.getCouple()))
                .coupleMemberState(toCoupleMemberState(entity.getCoupleMemberStateJpa()))
                .createdAt(entity.getCreatedAt())
                .modifiedAt(entity.getModifiedAt())
                .deletedAt(entity.getDeletedAt())
                .build();
    }

    public CoupleMemberEntity toEntity(CoupleMember domain) {
        return CoupleMemberEntity.builder()
                .id(domain.getId())
                .member(memberMapper.toEntity(domain.getMember()))
                .couple(coupleMapper.toEntity(domain.getCouple()))
                .coupleMemberStateJpa(toCoupleMemberStateJpa(domain.getCoupleMemberState()))
                .build();
    }

    private CoupleMemberState toCoupleMemberState(CoupleMemberStateJpa coupleMemberStateJpa) {
        return Optional.ofNullable(coupleMemberStateJpa)
                .map(cms -> CoupleMemberState.valueOf(cms.name()))
                .orElse(null);
    }

    private CoupleMemberStateJpa toCoupleMemberStateJpa(CoupleMemberState coupleMemberState) {
        return Optional.ofNullable(coupleMemberState)
                .map(cms -> CoupleMemberStateJpa.valueOf(cms.name()))
                .orElse(null);
    }
}