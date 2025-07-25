package makeus.cmc.malmo.adaptor.out.persistence.repository.custom;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import makeus.cmc.malmo.adaptor.out.persistence.MemberPersistenceAdapter;
import makeus.cmc.malmo.adaptor.out.persistence.entity.couple.QCoupleMemberEntity;
import makeus.cmc.malmo.adaptor.out.persistence.entity.member.QMemberEntity;
import makeus.cmc.malmo.adaptor.out.persistence.entity.value.InviteCodeEntityValue;
import makeus.cmc.malmo.application.port.out.LoadChatRoomMetadataPort;
import makeus.cmc.malmo.application.port.out.LoadPartnerPort;
import makeus.cmc.malmo.domain.value.state.CoupleMemberState;
import makeus.cmc.malmo.domain.value.state.CoupleState;
import makeus.cmc.malmo.domain.value.state.MemberState;

import java.util.Optional;

import static makeus.cmc.malmo.adaptor.out.persistence.entity.couple.QCoupleEntity.coupleEntity;
import static makeus.cmc.malmo.adaptor.out.persistence.entity.couple.QCoupleMemberEntity.coupleMemberEntity;
import static makeus.cmc.malmo.adaptor.out.persistence.entity.member.QMemberEntity.memberEntity;

@RequiredArgsConstructor
public class MemberRepositoryCustomImpl implements MemberRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Optional<MemberPersistenceAdapter.MemberResponseRepositoryDto> findMemberDetailsById(Long memberId) {
        MemberPersistenceAdapter.MemberResponseRepositoryDto dto = queryFactory
                .select(Projections.constructor(MemberPersistenceAdapter.MemberResponseRepositoryDto.class,
                        memberEntity.memberState.stringValue(),
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
                .leftJoin(coupleEntity).on(coupleEntity.id.eq(coupleMemberEntity.coupleEntityId.value))
                .where(memberEntity.id.eq(memberId))
                .fetchOne();

        return Optional.ofNullable(dto);
    }

    @Override
    public Optional<LoadPartnerPort.PartnerMemberRepositoryDto> findPartnerMember(Long memberId) {
        LoadPartnerPort.PartnerMemberRepositoryDto dto = queryFactory
                .select(Projections.constructor(LoadPartnerPort.PartnerMemberRepositoryDto.class,
                        memberEntity.memberState.stringValue(),
                        memberEntity.loveTypeCategory,
                        memberEntity.avoidanceRate,
                        memberEntity.anxietyRate,
                        memberEntity.nickname
                ))
                .from(coupleEntity)
                .join(coupleEntity.coupleMembers, coupleMemberEntity)
                .join(memberEntity).on(memberEntity.id.eq(coupleMemberEntity.memberEntityId.value))
                .where(coupleEntity.coupleState.ne(CoupleState.DELETED)
                                .and(coupleEntity.coupleMembers.any().memberEntityId.value.eq(memberId)
                                                .and(coupleMemberEntity.memberEntityId.value.ne(memberId))))
                .fetchOne();
        return Optional.ofNullable(dto);
    }

    @Override
    public boolean isCoupleMember(Long memberId) {
        Long count = queryFactory.select(coupleMemberEntity.count())
                .from(coupleMemberEntity)
                .join(memberEntity).on(memberEntity.id.eq(coupleMemberEntity.memberEntityId.value))
                .join(coupleEntity).on(coupleEntity.id.eq(coupleMemberEntity.coupleEntityId.value))
                .where(coupleMemberEntity.memberEntityId.value.eq(memberId)
                        .and(memberEntity.memberState.ne(MemberState.DELETED))
                        .and(coupleEntity.coupleState.ne(CoupleState.DELETED)))
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
    public boolean isAlreadyCoupleMemberByInviteCode(String inviteCode) {
        return queryFactory
                .selectOne()
                .from(coupleMemberEntity)
                .join(memberEntity).on(memberEntity.id.eq(coupleMemberEntity.memberEntityId.value))
                .join(coupleEntity).on(coupleEntity.id.eq(coupleMemberEntity.coupleEntityId.value)
                        .and(coupleEntity.coupleState.ne(CoupleState.DELETED)))
                .where(memberEntity.inviteCodeEntityValue.value.eq(inviteCode)
                        .and(coupleMemberEntity.coupleMemberState.eq(CoupleMemberState.ALIVE)))
                .fetchFirst() != null;
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
}
