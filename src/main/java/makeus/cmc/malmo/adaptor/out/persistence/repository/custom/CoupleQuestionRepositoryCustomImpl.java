package makeus.cmc.malmo.adaptor.out.persistence.repository.custom;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import makeus.cmc.malmo.adaptor.out.persistence.CoupleQuestionPersistenceAdapter;
import makeus.cmc.malmo.adaptor.out.persistence.entity.question.CoupleQuestionEntity;
import makeus.cmc.malmo.domain.value.state.CoupleMemberState;
import makeus.cmc.malmo.domain.value.state.CoupleQuestionState;

import java.util.Optional;

import static makeus.cmc.malmo.adaptor.out.persistence.entity.couple.QCoupleMemberEntity.coupleMemberEntity;
import static makeus.cmc.malmo.adaptor.out.persistence.entity.question.QCoupleQuestionEntity.coupleQuestionEntity;
import static makeus.cmc.malmo.adaptor.out.persistence.entity.question.QMemberAnswerEntity.memberAnswerEntity;

@RequiredArgsConstructor
public class CoupleQuestionRepositoryCustomImpl implements CoupleQuestionRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Optional<CoupleQuestionEntity> findTopLevelQuestionByCoupleId(Long coupleId) {
        CoupleQuestionEntity entity = queryFactory
                .selectFrom(coupleQuestionEntity)
                .where(coupleQuestionEntity.coupleEntityId.value.eq(coupleId))
                .orderBy(coupleQuestionEntity.question.level.asc())
                .limit(1)
                .fetchOne();

        return Optional.ofNullable(entity);
    }

    @Override
    public Optional<CoupleQuestionPersistenceAdapter.CoupleQuestionRepositoryDto> findTopLevelQuestionDto(Long memberId, Long coupleId) {
        CoupleQuestionPersistenceAdapter.CoupleQuestionRepositoryDto dto = queryFactory
                .select(Projections.constructor(CoupleQuestionPersistenceAdapter.CoupleQuestionRepositoryDto.class,
                        coupleQuestionEntity.id,
                        coupleQuestionEntity.question.title,
                        coupleQuestionEntity.question.content,
                        coupleQuestionEntity.question.level,
                        coupleQuestionEntity.coupleEntityId.value,
                        coupleQuestionEntity.coupleQuestionState,
                        coupleQuestionEntity.bothAnsweredAt,
                        JPAExpressions.selectOne()
                                .from(memberAnswerEntity)
                                .join(coupleMemberEntity)
                                .on(memberAnswerEntity.coupleMemberEntityId.value.eq(coupleMemberEntity.id))
                                .where(memberAnswerEntity.coupleQuestionEntityId.value.eq(coupleQuestionEntity.id)
                                        .and(coupleMemberEntity.memberEntityId.value.eq(memberId)))
                                .exists(),
                        JPAExpressions.selectOne()
                                .from(memberAnswerEntity)
                                .join(coupleMemberEntity)
                                .on(memberAnswerEntity.coupleMemberEntityId.value.eq(coupleMemberEntity.id))
                                .where(memberAnswerEntity.coupleQuestionEntityId.value.eq(coupleQuestionEntity.id)
                                        .and(coupleMemberEntity.memberEntityId.value.ne(memberId)))
                                .exists(),
                        coupleQuestionEntity.createdAt
                ))
                .from(coupleQuestionEntity)
                .where(coupleQuestionEntity.coupleEntityId.value.eq(coupleId))
                .orderBy(coupleQuestionEntity.question.level.desc())
                .limit(1)
                .fetchOne();

        return Optional.ofNullable(dto);
    }

    @Override
    public Optional<CoupleQuestionPersistenceAdapter.CoupleQuestionRepositoryDto> findQuestionDtoByLevel(Long memberId, Long coupleId, int level) {
        CoupleQuestionPersistenceAdapter.CoupleQuestionRepositoryDto dto = queryFactory
                .select(Projections.constructor(CoupleQuestionPersistenceAdapter.CoupleQuestionRepositoryDto.class,
                        coupleQuestionEntity.id,
                        coupleQuestionEntity.question.title,
                        coupleQuestionEntity.question.content,
                        coupleQuestionEntity.question.level,
                        coupleQuestionEntity.coupleEntityId.value,
                        coupleQuestionEntity.coupleQuestionState,
                        coupleQuestionEntity.bothAnsweredAt,
                        JPAExpressions.selectOne()
                                .from(memberAnswerEntity)
                                .join(coupleMemberEntity)
                                .on(memberAnswerEntity.coupleMemberEntityId.value.eq(coupleMemberEntity.id))
                                .where(memberAnswerEntity.coupleQuestionEntityId.value.eq(coupleQuestionEntity.id)
                                        .and(coupleMemberEntity.memberEntityId.value.eq(memberId)))
                                .exists(),
                        JPAExpressions.selectOne()
                                .from(memberAnswerEntity)
                                .join(coupleMemberEntity)
                                .on(memberAnswerEntity.coupleMemberEntityId.value.eq(coupleMemberEntity.id))
                                .where(memberAnswerEntity.coupleQuestionEntityId.value.eq(coupleQuestionEntity.id)
                                        .and(coupleMemberEntity.memberEntityId.value.ne(memberId)))
                                .exists(),
                        coupleQuestionEntity.createdAt
                ))
                .from(coupleQuestionEntity)
                .where(coupleQuestionEntity.coupleEntityId.value.eq(coupleId)
                        .and(coupleQuestionEntity.question.level.eq(level)))
                .fetchOne();

        return Optional.ofNullable(dto);
    }


    @Override
    public int countCoupleQuestionsByMemberId(Long memberId) {
        return queryFactory
                .select(coupleQuestionEntity.count().intValue())
                .from(coupleQuestionEntity)
                .join(coupleMemberEntity)
                .on(coupleMemberEntity.coupleEntityId.value.eq(coupleQuestionEntity.coupleEntityId.value))
                .where(coupleMemberEntity.memberEntityId.value.eq(memberId)
                        .and(coupleMemberEntity.coupleMemberState.ne(CoupleMemberState.DELETED))
                        .and(coupleQuestionEntity.coupleQuestionState.eq(CoupleQuestionState.COMPLETED)))
                .fetchOne();
    }
}
