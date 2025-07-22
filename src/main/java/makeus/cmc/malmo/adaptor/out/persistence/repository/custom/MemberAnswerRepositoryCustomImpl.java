package makeus.cmc.malmo.adaptor.out.persistence.repository.custom;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import makeus.cmc.malmo.adaptor.out.persistence.MemberAnswerPersistenceAdapter;
import makeus.cmc.malmo.adaptor.out.persistence.entity.couple.QCoupleMemberEntity;
import makeus.cmc.malmo.adaptor.out.persistence.entity.member.QMemberEntity;
import makeus.cmc.malmo.adaptor.out.persistence.entity.question.MemberAnswerEntity;
import makeus.cmc.malmo.adaptor.out.persistence.entity.question.QMemberAnswerEntity;
import makeus.cmc.malmo.domain.value.state.CoupleQuestionState;

import java.util.Optional;

import static makeus.cmc.malmo.adaptor.out.persistence.entity.couple.QCoupleMemberEntity.coupleMemberEntity;
import static makeus.cmc.malmo.adaptor.out.persistence.entity.member.QMemberEntity.memberEntity;
import static makeus.cmc.malmo.adaptor.out.persistence.entity.question.QCoupleQuestionEntity.coupleQuestionEntity;
import static makeus.cmc.malmo.adaptor.out.persistence.entity.question.QMemberAnswerEntity.memberAnswerEntity;

@RequiredArgsConstructor
public class MemberAnswerRepositoryCustomImpl implements MemberAnswerRepositoryCustom{

    private final JPAQueryFactory queryFactory;

    @Override
    public Optional<MemberAnswerPersistenceAdapter.AnswerRepositoryDto> findAnswersDtoByCoupleQuestionId(Long memberId, Long coupleQuestionId) {
        QMemberEntity partnerMemberEntity = new QMemberEntity("partnerMemberEntity");
        QMemberAnswerEntity partnerAnswerEntity = new QMemberAnswerEntity("partnerAnswerEntity");
        QCoupleMemberEntity coupleMemberEntity = new QCoupleMemberEntity("coupleMemberEntity");
        QCoupleMemberEntity partnerCoupleMemberEntity = new QCoupleMemberEntity("partnerCoupleMemberEntity");

        MemberAnswerPersistenceAdapter.AnswerRepositoryDto result = queryFactory
                .select(Projections.constructor(MemberAnswerPersistenceAdapter.AnswerRepositoryDto.class,
                        memberEntity.nickname,
                        memberAnswerEntity.answer,
                        coupleQuestionEntity.coupleQuestionState.ne(CoupleQuestionState.OUTDATED),
                        partnerMemberEntity.nickname,
                        partnerAnswerEntity.answer,
                        coupleQuestionEntity.coupleQuestionState.ne(CoupleQuestionState.OUTDATED)))
                .from(memberAnswerEntity)
                .join(coupleMemberEntity).on(coupleMemberEntity.id.eq(memberAnswerEntity.coupleMemberEntityId.value))
                .join(memberEntity)
                .on(coupleMemberEntity.memberEntityId.value.eq(memberEntity.id))
                .join(coupleQuestionEntity).on(coupleQuestionEntity.id.eq(memberAnswerEntity.coupleQuestionEntityId.value))
                .leftJoin(partnerAnswerEntity).on(partnerAnswerEntity.coupleQuestionEntityId.eq(memberAnswerEntity.coupleQuestionEntityId)
                        .and(partnerAnswerEntity.coupleMemberEntityId.ne(memberAnswerEntity.coupleMemberEntityId)))
                .leftJoin(partnerCoupleMemberEntity).on(partnerCoupleMemberEntity.id.eq(partnerAnswerEntity.coupleMemberEntityId.value))
                .leftJoin(partnerMemberEntity)
                .on(partnerCoupleMemberEntity.memberEntityId.value.eq(partnerMemberEntity.id))
                .where(coupleQuestionEntity.id.eq(coupleQuestionId)
                        .and(memberEntity.id.eq(memberId)))
                .fetchOne();

        return Optional.ofNullable(result);
    }

    @Override
    public Optional<MemberAnswerEntity> findByCoupleQuestionIdAndCoupleMemberId(Long coupleQuestionEntityId, Long memberId) {
        MemberAnswerEntity result = queryFactory.selectFrom(memberAnswerEntity)
                .join(coupleMemberEntity)
                .on(memberAnswerEntity.coupleMemberEntityId.value.eq(coupleMemberEntity.coupleEntityId.value))
                .where(memberAnswerEntity.coupleMemberEntityId.value.eq(coupleQuestionEntityId)
                        .and(coupleMemberEntity.memberEntityId.value.eq(memberId)))
                .fetchOne();

        return Optional.ofNullable(result);
    }

    @Override
    public boolean existsByCoupleQuestionIdAndMemberId(Long coupleQuestionEntityId, Long memberId) {
        Long count = queryFactory.select(memberAnswerEntity.count())
                .from(memberAnswerEntity)
                .join(coupleMemberEntity)
                .on(memberAnswerEntity.coupleMemberEntityId.value.eq(coupleMemberEntity.id))
                .where(memberAnswerEntity.coupleQuestionEntityId.value.eq(coupleQuestionEntityId)
                        .and(coupleMemberEntity.memberEntityId.value.eq(memberId)))
                .fetchOne();

        return count != null && count > 0;
    }

    @Override
    public Long countByCoupleQuestionIdAndMemberId(Long coupleQuestionEntityId) {
        return queryFactory.select(memberAnswerEntity.count())
                .from(memberAnswerEntity)
                .where(memberAnswerEntity.coupleQuestionEntityId.value.eq(coupleQuestionEntityId))
                .fetchOne();
    }
}
