package makeus.cmc.malmo.adaptor.out.persistence.mapper;

import lombok.RequiredArgsConstructor;
import makeus.cmc.malmo.adaptor.out.persistence.entity.member.MemberEntity;
import makeus.cmc.malmo.adaptor.out.persistence.entity.member.MemberRoleJpa;
import makeus.cmc.malmo.adaptor.out.persistence.entity.member.MemberStateJpa;
import makeus.cmc.malmo.adaptor.out.persistence.entity.member.ProviderJpa;
import makeus.cmc.malmo.adaptor.out.persistence.entity.value.InviteCodeEntityValue;
import makeus.cmc.malmo.adaptor.out.persistence.entity.value.LoveTypeEntityId;
import makeus.cmc.malmo.domain.model.member.Member;
import makeus.cmc.malmo.domain.model.member.MemberRole;
import makeus.cmc.malmo.domain.model.member.MemberState;
import makeus.cmc.malmo.domain.model.member.Provider;
import makeus.cmc.malmo.domain.model.value.InviteCodeValue;
import makeus.cmc.malmo.domain.model.value.LoveTypeId;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class MemberMapper {

    public Member toDomain(MemberEntity entity) {
        return Member.builder()
                .id(entity.getId())
                .provider(toProvider(entity.getProviderJpa()))
                .providerId(entity.getProviderId())
                .memberRole(toMemberRole(entity.getMemberRoleJpa()))
                .memberState(toMemberState(entity.getMemberStateJpa()))
                .isAlarmOn(entity.isAlarmOn())
                .firebaseToken(entity.getFirebaseToken())
                .refreshToken(entity.getRefreshToken())
                .loveTypeCategory(entity.getLoveTypeCategory())
                .avoidanceRate(entity.getAvoidanceRate())
                .anxietyRate(entity.getAnxietyRate())
                .nickname(entity.getNickname())
                .email(entity.getEmail())
                .inviteCode(
                        entity.getInviteCodeEntityValue() != null ? InviteCodeValue.of(entity.getInviteCodeEntityValue().getValue()) : null
                )
                .startLoveDate(entity.getStartLoveDate())
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
                .refreshToken(domain.getRefreshToken())
                .loveTypeCategory(domain.getLoveTypeCategory())
                .avoidanceRate(domain.getAvoidanceRate())
                .anxietyRate(domain.getAnxietyRate())
                .email(domain.getEmail())
                .nickname(domain.getNickname())
                .inviteCodeEntityValue(
                        domain.getInviteCode() != null ? InviteCodeEntityValue.of(domain.getInviteCode().getValue()) : null
                )
                .startLoveDate(domain.getStartLoveDate())
                .createdAt(domain.getCreatedAt())
                .modifiedAt(domain.getModifiedAt())
                .deletedAt(domain.getDeletedAt())
                .build();
    }

    private Provider toProvider(ProviderJpa providerJpa) {
        return providerJpa != null ? Provider.valueOf(providerJpa.name()) : null;
    }

    private ProviderJpa toProviderJpa(Provider provider) {
        return provider != null ? ProviderJpa.valueOf(provider.name()) : null;
    }

    private MemberRole toMemberRole(MemberRoleJpa memberRoleJpa) {
        return memberRoleJpa != null ? MemberRole.valueOf(memberRoleJpa.name()) : null;
    }

    private MemberRoleJpa toMemberRoleJpa(MemberRole memberRole) {
        return memberRole != null ? MemberRoleJpa.valueOf(memberRole.name()) : null;
    }

    private MemberState toMemberState(MemberStateJpa memberStateJpa) {
        return memberStateJpa != null ? MemberState.valueOf(memberStateJpa.name()) : null;
    }

    private MemberStateJpa toMemberStateJpa(MemberState memberState) {
        return memberState != null ? MemberStateJpa.valueOf(memberState.name()) : null;
    }
}