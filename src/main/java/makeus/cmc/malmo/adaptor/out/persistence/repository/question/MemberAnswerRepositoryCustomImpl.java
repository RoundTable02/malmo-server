package makeus.cmc.malmo.adaptor.out.persistence.repository.question;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import makeus.cmc.malmo.adaptor.out.persistence.adapter.MemberAnswerPersistenceAdapter;
import makeus.cmc.malmo.adaptor.out.persistence.entity.couple.QCoupleEntity;
import makeus.cmc.malmo.adaptor.out.persistence.entity.couple.QCoupleMemberEntity;
import makeus.cmc.malmo.adaptor.out.persistence.entity.member.QMemberEntity;
import makeus.cmc.malmo.adaptor.out.persistence.entity.question.MemberAnswerEntity;
import makeus.cmc.malmo.adaptor.out.persistence.entity.question.QCoupleQuestionEntity;
import makeus.cmc.malmo.adaptor.out.persistence.entity.question.QMemberAnswerEntity;
import makeus.cmc.malmo.adaptor.out.persistence.entity.question.QQuestionEntity;
import makeus.cmc.malmo.domain.value.state.CoupleMemberState;
import makeus.cmc.malmo.domain.value.state.CoupleQuestionState;

import java.util.Optional;

import static makeus.cmc.malmo.adaptor.out.persistence.entity.couple.QCoupleMemberEntity.coupleMemberEntity;
import static makeus.cmc.malmo.adaptor.out.persistence.entity.question.QMemberAnswerEntity.memberAnswerEntity;

@RequiredArgsConstructor
public class MemberAnswerRepositoryCustomImpl implements MemberAnswerRepositoryCustom{

    private final JPAQueryFactory queryFactory;

    @Override
    public Optional<MemberAnswerPersistenceAdapter.AnswerRepositoryDto> findAnswersDtoByCoupleQuestionId(Long memberId, Long coupleQuestionId) {
        QCoupleQuestionEntity coupleQuestion = QCoupleQuestionEntity.coupleQuestionEntity;
        QQuestionEntity question = QQuestionEntity.questionEntity;
        QCoupleEntity couple = QCoupleEntity.coupleEntity;

        QCoupleMemberEntity myCoupleMember = QCoupleMemberEntity.coupleMemberEntity;
        QMemberEntity me = QMemberEntity.memberEntity;
        QMemberAnswerEntity myAnswer = QMemberAnswerEntity.memberAnswerEntity;

        QCoupleMemberEntity partnerCoupleMember = new QCoupleMemberEntity("partnerCoupleMember");
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
                        partner.nickname,
                        partnerAnswer.answer,
                        new CaseBuilder()
                                .when(coupleQuestion.coupleQuestionState.ne(CoupleQuestionState.OUTDATED))
                                .then(true)
                                .otherwise(false)
                ))
                .from(coupleQuestion)
                .join(coupleQuestion.question, question)
                .join(couple).on(coupleQuestion.coupleEntityId.value.eq(couple.id))
                .join(myCoupleMember).on(
                        myCoupleMember.coupleEntityId.value.eq(couple.id)
                                .and(myCoupleMember.memberEntityId.value.eq(memberId))
                )
                .join(me).on(myCoupleMember.memberEntityId.value.eq(me.id))
                .leftJoin(myAnswer).on(
                        myAnswer.coupleQuestionEntityId.value.eq(coupleQuestion.id)
                                .and(myAnswer.coupleMemberEntityId.value.eq(myCoupleMember.id))
                )
                .leftJoin(partnerCoupleMember).on(
                        partnerCoupleMember.coupleEntityId.value.eq(couple.id)
                                .and(partnerCoupleMember.id.ne(myCoupleMember.id))
                )
                .leftJoin(partner).on(partnerCoupleMember.memberEntityId.value.eq(partner.id))
                .leftJoin(partnerAnswer).on(
                        partnerAnswer.coupleQuestionEntityId.value.eq(coupleQuestion.id)
                                .and(partnerAnswer.coupleMemberEntityId.value.eq(partnerCoupleMember.id))
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
                .join(coupleMemberEntity)
                .on(memberAnswerEntity.coupleMemberEntityId.value.eq(coupleMemberEntity.id))
                .where(memberAnswerEntity.coupleQuestionEntityId.value.eq(coupleQuestionEntityId)
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
