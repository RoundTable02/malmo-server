package makeus.cmc.malmo.adaptor.out.persistence.repository.couple;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import makeus.cmc.malmo.adaptor.out.persistence.entity.couple.CoupleEntity;
import makeus.cmc.malmo.domain.value.state.CoupleMemberState;
import makeus.cmc.malmo.domain.value.state.CoupleState;

import java.util.Optional;

import static makeus.cmc.malmo.adaptor.out.persistence.entity.couple.QCoupleEntity.coupleEntity;
import static makeus.cmc.malmo.adaptor.out.persistence.entity.member.QMemberEntity.memberEntity;

@RequiredArgsConstructor
public class CoupleRepositoryCustomImpl implements CoupleRepositoryCustom{

    private final JPAQueryFactory queryFactory;

    @Override
    public Optional<CoupleEntity> findCoupleByMemberId(Long memberId) {
        CoupleEntity result = queryFactory.select(coupleEntity)
                .from(memberEntity)
                .join(coupleEntity).on(memberEntity.coupleEntityId.value.eq(coupleEntity.id))
                .where(memberEntity.id.eq(memberId))
                .fetchOne();

        return Optional.ofNullable(result);
    }

    @Override
    public Optional<CoupleEntity> findCoupleByMemberIdAndPartnerId(Long memberId, Long partnerId) {
        CoupleEntity result = queryFactory.selectFrom(coupleEntity)
                .where(
                        (coupleEntity.firstMemberId.value.eq(memberId)
                                .and(coupleEntity.secondMemberId.value.eq(partnerId)))
                                .or(coupleEntity.firstMemberId.value.eq(partnerId)
                                        .and(coupleEntity.secondMemberId.value.eq(memberId)))
                )
                .fetchOne();

        return Optional.ofNullable(result);
    }
}
