package makeus.cmc.malmo.adaptor.out.persistence.entity.member;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import makeus.cmc.malmo.adaptor.out.persistence.entity.value.InviteCodeEntityValue;
import makeus.cmc.malmo.adaptor.out.persistence.entity.value.LoveTypeEntityId;
import makeus.cmc.malmo.domain.model.BaseTimeEntity;
import makeus.cmc.malmo.domain.model.love_type.LoveTypeCategory;

import java.time.LocalDate;

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

    @Enumerated(value = EnumType.STRING)
    private LoveTypeCategory loveTypeCategory;

    private float avoidanceRate;

    private float anxietyRate;

    private String nickname;

    private String email;

    @Embedded
    private InviteCodeEntityValue inviteCodeEntityValue;

    private LocalDate startLoveDate;
}
