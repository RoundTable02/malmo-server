package makeus.cmc.malmo.adaptor.out.persistence.repository.question;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import makeus.cmc.malmo.adaptor.out.persistence.adapter.MemberAnswerPersistenceAdapter;
import makeus.cmc.malmo.adaptor.out.persistence.entity.couple.QCoupleEntity;
import makeus.cmc.malmo.adaptor.out.persistence.entity.member.QMemberEntity;
import makeus.cmc.malmo.adaptor.out.persistence.entity.question.MemberAnswerEntity;
import makeus.cmc.malmo.adaptor.out.persistence.entity.question.QCoupleQuestionEntity;
import makeus.cmc.malmo.adaptor.out.persistence.entity.question.QMemberAnswerEntity;
import makeus.cmc.malmo.adaptor.out.persistence.entity.question.QQuestionEntity;
import makeus.cmc.malmo.domain.value.state.CoupleQuestionState;

import java.util.Optional;

import static makeus.cmc.malmo.adaptor.out.persistence.entity.couple.QCoupleEntity.coupleEntity;
import static makeus.cmc.malmo.adaptor.out.persistence.entity.question.QMemberAnswerEntity.memberAnswerEntity;

@RequiredArgsConstructor
public class MemberAnswerRepositoryCustomImpl implements MemberAnswerRepositoryCustom{

    private final JPAQueryFactory queryFactory;

    @Override
    public Optional<MemberAnswerPersistenceAdapter.AnswerRepositoryDto> findAnswersDtoByCoupleQuestionId(Long memberId, Long coupleQuestionId) {
        QCoupleQuestionEntity coupleQuestion = QCoupleQuestionEntity.coupleQuestionEntity;
        QQuestionEntity question = QQuestionEntity.questionEntity;
        QCoupleEntity couple = QCoupleEntity.coupleEntity;

        QMemberEntity me = QMemberEntity.memberEntity;
        QMemberAnswerEntity myAnswer = QMemberAnswerEntity.memberAnswerEntity;

        QMemberEntity partner = new QMemberEntity("partner");
        QMemberAnswerEntity partnerAnswer = new QMemberAnswerEntity("partnerAnswer");

        MemberAnswerPersistenceAdapter.AnswerRepositoryDto result = queryFactory
                .select(Projections.constructor(MemberAnswerPersistenceAdapter.AnswerRepositoryDto.class,
                        question.title,
                        question.content,
                        question.level,
                        myAnswer.createdAt,
                        me.nickname,
                        myAnswer.answer,
                        new CaseBuilder()
                                .when(coupleQuestion.coupleQuestionState.ne(CoupleQuestionState.OUTDATED))
                                .then(true)
                                .otherwise(false),
                        new CaseBuilder()
                                .when(partner.coupleEntityId.value.eq(coupleEntity.id))
                                .then(partner.nickname)
                                .otherwise(
                                        new CaseBuilder()
                                                .when(coupleEntity.firstMemberId.value.eq(memberId))
                                                .then(coupleEntity.secondMemberSnapshot.nickname)
                                                .otherwise(coupleEntity.firstMemberSnapshot.nickname)
                                ),
                        partnerAnswer.answer,
                        new CaseBuilder()
                                .when(coupleQuestion.coupleQuestionState.ne(CoupleQuestionState.OUTDATED))
                                .then(true)
                                .otherwise(false)
                ))
                .from(coupleQuestion)
                .join(coupleQuestion.question, question)
                .join(couple).on(coupleQuestion.coupleEntityId.value.eq(couple.id))
                .join(me).on(me.coupleEntityId.value.eq(couple.id).and(me.id.eq(memberId)))
                .leftJoin(myAnswer).on(
                        myAnswer.coupleQuestionEntityId.value.eq(coupleQuestion.id)
                                .and(myAnswer.memberEntityId.value.eq(me.id))
                )
                .leftJoin(partner).on(
                        partner.coupleEntityId.value.eq(couple.id)
                                .and(partner.id.ne(me.id))
                )
                .leftJoin(partnerAnswer).on(
                        partnerAnswer.coupleQuestionEntityId.value.eq(coupleQuestion.id)
                                .and(partnerAnswer.memberEntityId.value.eq(partner.id))
                )
                .where(
                        coupleQuestion.id.eq(coupleQuestionId)
                )
                .fetchOne();

        return Optional.ofNullable(result);
    }

    @Override
    public Optional<MemberAnswerEntity> findByCoupleQuestionIdAndMemberId(Long coupleQuestionEntityId, Long memberId) {
        MemberAnswerEntity result = queryFactory.selectFrom(memberAnswerEntity)
                .where(memberAnswerEntity.coupleQuestionEntityId.value.eq(coupleQuestionEntityId)
                        .and(memberAnswerEntity.memberEntityId.value.eq(memberId)))
                .fetchOne();

        return Optional.ofNullable(result);
    }

    @Override
    public boolean existsByCoupleQuestionIdAndMemberId(Long coupleQuestionEntityId, Long memberId) {
        Long count = queryFactory.select(memberAnswerEntity.count())
                .from(memberAnswerEntity)
                .where(memberAnswerEntity.coupleQuestionEntityId.value.eq(coupleQuestionEntityId)
                        .and(memberAnswerEntity.memberEntityId.value.eq(memberId)))
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
