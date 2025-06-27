package makeus.cmc.malmo.adaptor.out.persistence.entity.member;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import makeus.cmc.malmo.adaptor.out.persistence.entity.LoveTypeEntity;
import makeus.cmc.malmo.domain.model.BaseTimeEntity;

@Getter
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class MemberEntity extends BaseTimeEntity {

    @Column(name = "memberId")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(value = EnumType.STRING)
    private ProviderJpa providerJpa;

    private String providerId;

    @Enumerated(value = EnumType.STRING)
    private MemberRoleJpa memberRoleJpa;

    @Enumerated(value = EnumType.STRING)
    private MemberStateJpa memberStateJpa;

    private boolean isAlarmOn;

    private String firebaseToken;

    @ManyToOne
    @JoinColumn(name = "love_type_id")
    private LoveTypeEntity loveType;

    private float avoidanceRate;

    private float anxietyRate;

    private String nickname;

    public static MemberEntity createMember(ProviderJpa providerJpa, String providerId, MemberRoleJpa memberRoleJpa, MemberStateJpa memberStateJpa, LoveTypeEntity loveType) {
        MemberEntity member = new MemberEntity();
        member.providerJpa = providerJpa;
        member.providerId = providerId;
        member.memberRoleJpa = memberRoleJpa;
        member.memberStateJpa = memberStateJpa;
        member.loveType = loveType;
        member.isAlarmOn = true; // 기본값으로 알림 설정
        return member;
    }
}
