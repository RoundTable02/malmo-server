package makeus.cmc.malmo.adaptor.out.persistence.repository.custom;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import makeus.cmc.malmo.adaptor.out.persistence.entity.couple.CoupleEntity;
import makeus.cmc.malmo.domain.value.state.CoupleMemberState;
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
                        .and(coupleMemberEntity.coupleMemberState.eq(CoupleMemberState.ALIVE)))
                .fetchOne();

        return Optional.ofNullable(result);
    }

    @Override
    public void deleteCoupleByMemberId(Long memberId) {
        queryFactory.update(coupleEntity)
                .set(coupleEntity.coupleState, CoupleState.DELETED)
                .where(coupleEntity.id.in(
                        queryFactory.select(coupleMemberEntity.coupleEntityId.value)
                                .from(coupleMemberEntity)
                                .where(coupleMemberEntity.memberEntityId.value.eq(memberId)))
                        .and(coupleEntity.coupleState.eq(CoupleState.ALIVE)))
                .execute();
    }
}
