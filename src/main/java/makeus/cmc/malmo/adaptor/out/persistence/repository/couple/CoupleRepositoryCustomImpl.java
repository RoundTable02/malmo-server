package makeus.cmc.malmo.adaptor.out.persistence.repository.couple;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import makeus.cmc.malmo.adaptor.out.persistence.entity.couple.CoupleEntity;
import makeus.cmc.malmo.domain.value.state.CoupleState;

import java.util.Optional;

import static makeus.cmc.malmo.adaptor.out.persistence.entity.couple.QCoupleEntity.coupleEntity;
import static makeus.cmc.malmo.adaptor.out.persistence.entity.couple.QCoupleMemberEntity.coupleMemberEntity;

@RequiredArgsConstructor
public class CoupleRepositoryCustomImpl implements CoupleRepositoryCustom{

    private final JPAQueryFactory queryFactory;

    @Override
    public Optional<CoupleEntity> findCoupleByMemberId(Long memberId) {
        CoupleEntity result = queryFactory.selectFrom(coupleEntity)
                .join(coupleMemberEntity)
                .on(coupleEntity.id.eq(coupleMemberEntity.coupleEntityId.value))
                .where(coupleMemberEntity.memberEntityId.value.eq(memberId)
                        .and(coupleEntity.coupleState.ne(CoupleState.DELETED)))
                .fetchOne();

        return Optional.ofNullable(result);
    }

    @Override
    public Optional<CoupleEntity> findCoupleByMemberIdAndPartnerId(Long memberId, Long partnerId) {
        CoupleEntity result = queryFactory.selectFrom(coupleEntity)
                .join(coupleMemberEntity)
                .on(coupleEntity.id.eq(coupleMemberEntity.coupleEntityId.value))
                .where(coupleEntity.coupleMembers.any().memberEntityId.value.eq(memberId)
                        .and(coupleEntity.coupleMembers.any().memberEntityId.value.eq(partnerId)))
                .fetchOne();

        return Optional.ofNullable(result);
    }
}
