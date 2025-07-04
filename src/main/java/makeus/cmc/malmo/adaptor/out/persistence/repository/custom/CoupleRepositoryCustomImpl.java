package makeus.cmc.malmo.adaptor.out.persistence.repository.custom;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import makeus.cmc.malmo.application.port.out.LoadPartnerPort;

import java.util.Optional;

import static makeus.cmc.malmo.adaptor.out.persistence.entity.couple.QCoupleEntity.coupleEntity;
import static makeus.cmc.malmo.adaptor.out.persistence.entity.couple.QCoupleMemberEntity.coupleMemberEntity;
import static makeus.cmc.malmo.adaptor.out.persistence.entity.member.QMemberEntity.memberEntity;

@RequiredArgsConstructor
public class CoupleRepositoryCustomImpl implements CoupleRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Optional<LoadPartnerPort.PartnerMemberRepositoryDto> findPartnerMember(Long memberId) {
        LoadPartnerPort.PartnerMemberRepositoryDto dto = queryFactory
                .select(Projections.constructor(LoadPartnerPort.PartnerMemberRepositoryDto.class,
                        coupleEntity.startLoveDate,
                        memberEntity.memberStateJpa.stringValue(),
                        memberEntity.loveType.title.coalesce(""),
                        memberEntity.avoidanceRate,
                        memberEntity.anxietyRate,
                        memberEntity.nickname
                ))
                .from(coupleEntity)
                .join(coupleEntity.coupleMembers, coupleMemberEntity)
                .join(memberEntity).on(memberEntity.id.eq(coupleMemberEntity.memberEntityId.value))
                .leftJoin(memberEntity.loveType)
                .where(
                        coupleEntity.coupleMembers.any().memberEntityId.value.eq(memberId)
                                .and(coupleMemberEntity.memberEntityId.value.ne(memberId))
                )
                .fetchFirst();
        return Optional.ofNullable(dto);
    }
}
