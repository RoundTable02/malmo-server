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

    private String refreshToken;

    @ManyToOne
    @JoinColumn(name = "love_type_id")
    private LoveTypeEntity loveType;

    private float avoidanceRate;

    private float anxietyRate;

    private String nickname;

    private String email;
}
