package makeus.cmc.malmo.adaptor.out.persistence.repository.member;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import makeus.cmc.malmo.adaptor.out.persistence.adapter.MemberPersistenceAdapter;
import makeus.cmc.malmo.adaptor.out.persistence.entity.couple.CoupleMemberEntity;
import makeus.cmc.malmo.adaptor.out.persistence.entity.couple.QCoupleMemberEntity;
import makeus.cmc.malmo.adaptor.out.persistence.entity.member.QMemberEntity;
import makeus.cmc.malmo.adaptor.out.persistence.entity.value.InviteCodeEntityValue;
import makeus.cmc.malmo.application.port.out.chat.LoadChatRoomMetadataPort;
import makeus.cmc.malmo.domain.value.state.CoupleMemberState;
import makeus.cmc.malmo.domain.value.state.CoupleState;
import makeus.cmc.malmo.domain.value.state.MemberState;

import java.util.Optional;

import static makeus.cmc.malmo.adaptor.out.persistence.entity.couple.QCoupleEntity.coupleEntity;
import static makeus.cmc.malmo.adaptor.out.persistence.entity.couple.QCoupleMemberEntity.coupleMemberEntity;
import static makeus.cmc.malmo.adaptor.out.persistence.entity.member.QMemberEntity.memberEntity;

@Slf4j
@RequiredArgsConstructor
public class MemberRepositoryCustomImpl implements MemberRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Optional<MemberPersistenceAdapter.MemberResponseRepositoryDto> findMemberDetailsById(Long memberId) {
        MemberPersistenceAdapter.MemberResponseRepositoryDto dto = queryFactory
                .select(Projections.constructor(MemberPersistenceAdapter.MemberResponseRepositoryDto.class,
                        memberEntity.memberState.stringValue(),
                        memberEntity.provider,
                        coupleEntity.startLoveDate.coalesce(memberEntity.startLoveDate),
                        memberEntity.loveTypeCategory,
                        memberEntity.avoidanceRate,
                        memberEntity.anxietyRate,
                        memberEntity.nickname,
                        memberEntity.email
                ))
                .from(memberEntity)
                .leftJoin(coupleMemberEntity).on(coupleMemberEntity.memberEntityId.value.eq(memberEntity.id)
                        .and(coupleMemberEntity.coupleMemberState.ne(CoupleMemberState.DELETED)))
                .leftJoin(coupleEntity)
                .on(coupleEntity.id.eq(coupleMemberEntity.coupleEntityId.value)
                        .and(JPAExpressions
                                .selectFrom(coupleMemberEntity)
                                .where(coupleMemberEntity.coupleEntityId.value.eq(coupleEntity.id)
                                        .and(coupleMemberEntity.coupleMemberState.eq(CoupleMemberState.DELETED)))
                                .notExists()))
                .where(memberEntity.id.eq(memberId))
                .fetchOne();

        return Optional.ofNullable(dto);
    }

    @Override
    public Optional<MemberPersistenceAdapter.PartnerMemberRepositoryDto> findPartnerMember(Long memberId) {
        MemberPersistenceAdapter.PartnerMemberRepositoryDto dto = queryFactory
                .select(Projections.constructor(MemberPersistenceAdapter.PartnerMemberRepositoryDto.class,
                        coupleMemberEntity.coupleMemberState.stringValue(),
                        memberEntity.loveTypeCategory,
                        memberEntity.avoidanceRate,
                        memberEntity.anxietyRate,
                        memberEntity.nickname
                ))
                .from(coupleEntity)
                .join(coupleEntity.coupleMembers, coupleMemberEntity)
                .join(memberEntity).on(memberEntity.id.eq(coupleMemberEntity.memberEntityId.value))
                .where(coupleEntity.id.in(
                                JPAExpressions.select(coupleMemberEntity.coupleEntityId.value)
                                        .from(coupleMemberEntity)
                                        .where(coupleMemberEntity.memberEntityId.value.eq(memberId)
                                                .and(coupleMemberEntity.coupleMemberState.ne(CoupleMemberState.DELETED)))
                        )
                        .and(coupleMemberEntity.memberEntityId.value.ne(memberId)))
                .fetchOne();

        return Optional.ofNullable(dto);
    }

    @Override
    public boolean isCoupleMember(Long memberId) {
        Long count = queryFactory.select(coupleMemberEntity.count())
                .from(coupleMemberEntity)
                .join(memberEntity).on(memberEntity.id.eq(coupleMemberEntity.memberEntityId.value))
                .where(coupleMemberEntity.memberEntityId.value.eq(memberId)
                        .and(coupleMemberEntity.coupleMemberState.ne(CoupleMemberState.DELETED))
                        .and(memberEntity.memberState.ne(MemberState.DELETED)))
                .fetchOne();

        return count != null && count > 0;
    }

    @Override
    public boolean existsByInviteCode(String inviteCode) {
        return queryFactory
                .selectFrom(memberEntity)
                .where(memberEntity.inviteCodeEntityValue.value.eq(inviteCode))
                .fetchFirst() != null;
    }

    @Override
    public boolean isPartnerCoupleMemberAlive(Long memberId) {
        CoupleMemberEntity coupleMemberEntity1 = queryFactory
                .selectFrom(coupleMemberEntity)
                .join(memberEntity).on(memberEntity.id.eq(coupleMemberEntity.memberEntityId.value))
                .join(coupleEntity).on(coupleEntity.id.eq(coupleMemberEntity.coupleEntityId.value)
                        .and(coupleEntity.coupleState.ne(CoupleState.DELETED)))
                .where(coupleEntity.id.in(
                                JPAExpressions.select(coupleMemberEntity.coupleEntityId.value)
                                        .from(coupleMemberEntity)
                                        .where(coupleMemberEntity.memberEntityId.value.eq(memberId)
                                                .and(coupleMemberEntity.coupleMemberState.ne(CoupleMemberState.DELETED)))
                        )
                        .and(coupleMemberEntity.memberEntityId.value.ne(memberId))
                        .and(coupleMemberEntity.coupleMemberState.ne(CoupleMemberState.DELETED)))
                .fetchFirst();


        log.info("Is partner couple member alive for memberId {}: {}", memberId, coupleMemberEntity1 != null);
        return coupleMemberEntity1 != null;
    }

    @Override
    public Optional<InviteCodeEntityValue> findInviteCodeByMemberId(Long memberId) {
        InviteCodeEntityValue inviteCodeEntityValue = queryFactory
                .select(memberEntity.inviteCodeEntityValue)
                .from(memberEntity)
                .where(memberEntity.id.eq(memberId))
                .fetchOne();

        return Optional.ofNullable(inviteCodeEntityValue);
    }

    @Override
    public Optional<LoadChatRoomMetadataPort.ChatRoomMetadataDto> loadChatRoomMetadata(Long memberId) {
        QMemberEntity partnerMemberEntity = new QMemberEntity("partnerMemberEntity");
        QCoupleMemberEntity partnerCoupleMemberEntity = new QCoupleMemberEntity("partnerCoupleMemberEntity");

        LoadChatRoomMetadataPort.ChatRoomMetadataDto dto = queryFactory
                .select(Projections.constructor(
                        LoadChatRoomMetadataPort.ChatRoomMetadataDto.class,
                        memberEntity.loveTypeCategory,
                        partnerMemberEntity.loveTypeCategory
                ))
                .from(memberEntity)
                .leftJoin(coupleMemberEntity).on(coupleMemberEntity.memberEntityId.value.eq(memberEntity.id)
                        .and(coupleMemberEntity.coupleMemberState.ne(CoupleMemberState.DELETED)))
                .leftJoin(coupleEntity).on(coupleEntity.id.eq(coupleMemberEntity.coupleEntityId.value))
                .leftJoin(partnerCoupleMemberEntity)
                .on(partnerCoupleMemberEntity.coupleEntityId.value.eq(coupleEntity.id)
                        .and(partnerCoupleMemberEntity.memberEntityId.value.ne(memberId))
                        .and(partnerCoupleMemberEntity.coupleMemberState.ne(CoupleMemberState.DELETED)))
                .leftJoin(partnerMemberEntity).on(partnerMemberEntity.id.eq(partnerCoupleMemberEntity.memberEntityId.value))
                .where(memberEntity.id.eq(memberId))
                .fetchOne();

        return Optional.ofNullable(dto);
    }

    @Override
    public boolean isMemberStateAlive(Long memberId) {
        Long count = queryFactory.select(memberEntity.count())
            .from(memberEntity)
            .where(memberEntity.id.eq(memberId)
                    .and(memberEntity.memberState.ne(MemberState.DELETED)))
            .fetchOne();

        return count != null && count > 0;
    }

    @Override
    public Optional<Long> findPartnerMemberId(Long memberId) {
        Long partnerMemberId = queryFactory
                .select(coupleMemberEntity.memberEntityId.value)
                .from(coupleEntity)
                .join(coupleEntity.coupleMembers, coupleMemberEntity)
                .join(memberEntity).on(memberEntity.id.eq(coupleMemberEntity.memberEntityId.value))
                .where(coupleEntity.id.in(
                                JPAExpressions.select(coupleMemberEntity.coupleEntityId.value)
                                        .from(coupleMemberEntity)
                                        .where(coupleMemberEntity.memberEntityId.value.eq(memberId)
                                                .and(coupleMemberEntity.coupleMemberState.ne(CoupleMemberState.DELETED)))
                        )
                        .and(coupleMemberEntity.memberEntityId.value.ne(memberId)))
                .fetchOne();

        return Optional.ofNullable(partnerMemberId);
    }
}
