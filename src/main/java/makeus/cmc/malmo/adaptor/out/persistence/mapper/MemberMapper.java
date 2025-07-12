package makeus.cmc.malmo.adaptor.out.persistence.mapper;

import lombok.RequiredArgsConstructor;
import makeus.cmc.malmo.adaptor.out.persistence.entity.member.MemberEntity;
import makeus.cmc.malmo.adaptor.out.persistence.entity.value.InviteCodeEntityValue;
import makeus.cmc.malmo.adaptor.out.persistence.entity.value.LoveTypeEntityId;
import makeus.cmc.malmo.domain.model.member.Member;
import makeus.cmc.malmo.domain.value.id.InviteCodeValue;
import makeus.cmc.malmo.domain.value.id.LoveTypeId;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class MemberMapper {

    public Member toDomain(MemberEntity entity) {
        return Member.builder()
                .id(entity.getId())
                .provider(entity.getProvider())
                .providerId(entity.getProviderId())
                .memberRole(entity.getMemberRole())
                .memberState(entity.getMemberState())
                .isAlarmOn(entity.isAlarmOn())
                .firebaseToken(entity.getFirebaseToken())
                .refreshToken(entity.getRefreshToken())
                .loveTypeId(LoveTypeId.of(
                        entity.getLoveTypeEntityId() != null ? entity.getLoveTypeEntityId().getValue() : null
                ))
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
                .provider(domain.getProvider())
                .providerId(domain.getProviderId())
                .memberRole(domain.getMemberRole())
                .memberState(domain.getMemberState())
                .isAlarmOn(domain.isAlarmOn())
                .firebaseToken(domain.getFirebaseToken())
                .refreshToken(domain.getRefreshToken())
                .loveTypeEntityId(
                        domain.getLoveTypeId() != null ? LoveTypeEntityId.of(domain.getLoveTypeId().getValue()) : null
                )
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
}