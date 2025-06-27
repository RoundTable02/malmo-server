package makeus.cmc.malmo.adaptor.out.persistence.mapper;

import makeus.cmc.malmo.adaptor.out.persistence.entity.member.MemberEntity;
import makeus.cmc.malmo.adaptor.out.persistence.entity.member.ProviderJpa;
import makeus.cmc.malmo.adaptor.out.persistence.entity.member.MemberRoleJpa;
import makeus.cmc.malmo.adaptor.out.persistence.entity.member.MemberStateJpa;
import makeus.cmc.malmo.domain.model.member.Member;
import makeus.cmc.malmo.domain.model.member.Provider;
import makeus.cmc.malmo.domain.model.member.MemberRole;
import makeus.cmc.malmo.domain.model.member.MemberState;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class MemberMapper {

    private final LoveTypeMapper loveTypeMapper;

    public MemberMapper(LoveTypeMapper loveTypeMapper) {
        this.loveTypeMapper = loveTypeMapper;
    }

    public Member toDomain(MemberEntity entity) {
        return Member.builder()
                .id(entity.getId())
                .provider(toProvider(entity.getProviderJpa()))
                .providerId(entity.getProviderId())
                .memberRole(toMemberRole(entity.getMemberRoleJpa()))
                .memberState(toMemberState(entity.getMemberStateJpa()))
                .isAlarmOn(entity.isAlarmOn())
                .firebaseToken(entity.getFirebaseToken())
                .loveType(loveTypeMapper.toDomain(entity.getLoveType()))
                .avoidanceRate(entity.getAvoidanceRate())
                .anxietyRate(entity.getAnxietyRate())
                .nickname(entity.getNickname())
                .createdAt(entity.getCreatedAt())
                .modifiedAt(entity.getModifiedAt())
                .deletedAt(entity.getDeletedAt())
                .build();
    }

    public MemberEntity toEntity(Member domain) {
        return MemberEntity.builder()
                .id(domain.getId())
                .providerJpa(toProviderJpa(domain.getProvider()))
                .providerId(domain.getProviderId())
                .memberRoleJpa(toMemberRoleJpa(domain.getMemberRole()))
                .memberStateJpa(toMemberStateJpa(domain.getMemberState()))
                .isAlarmOn(domain.isAlarmOn())
                .firebaseToken(domain.getFirebaseToken())
                .loveType(loveTypeMapper.toEntity(domain.getLoveType()))
                .avoidanceRate(domain.getAvoidanceRate())
                .anxietyRate(domain.getAnxietyRate())
                .nickname(domain.getNickname())
                .build();
    }

    private Provider toProvider(ProviderJpa providerJpa) {
        return Optional.ofNullable(providerJpa)
                .map(p -> Provider.valueOf(p.name()))
                .orElse(null);
    }

    private ProviderJpa toProviderJpa(Provider provider) {
        return Optional.ofNullable(provider)
                .map(p -> ProviderJpa.valueOf(p.name()))
                .orElse(null);
    }

    private MemberRole toMemberRole(MemberRoleJpa memberRoleJpa) {
        return Optional.ofNullable(memberRoleJpa)
                .map(mr -> MemberRole.valueOf(mr.name()))
                .orElse(null);
    }

    private MemberRoleJpa toMemberRoleJpa(MemberRole memberRole) {
        return Optional.ofNullable(memberRole)
                .map(mr -> MemberRoleJpa.valueOf(mr.name()))
                .orElse(null);
    }

    private MemberState toMemberState(MemberStateJpa memberStateJpa) {
        return Optional.ofNullable(memberStateJpa)
                .map(ms -> MemberState.valueOf(ms.name()))
                .orElse(null);
    }

    private MemberStateJpa toMemberStateJpa(MemberState memberState) {
        return Optional.ofNullable(memberState)
                .map(ms -> MemberStateJpa.valueOf(ms.name()))
                .orElse(null);
    }
}