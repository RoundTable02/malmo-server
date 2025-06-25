package makeus.cmc.malmo.domain.model.member;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import makeus.cmc.malmo.domain.model.BaseTimeEntity;
import makeus.cmc.malmo.domain.model.LoveType;

@Getter
@NoArgsConstructor
@Entity
public class Member extends BaseTimeEntity {

    @Column(name = "memberId")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(value = EnumType.STRING)
    private Provider provider;

    private String providerId;

    @Enumerated(value = EnumType.STRING)
    private MemberRole memberRole;

    @Enumerated(value = EnumType.STRING)
    private MemberState memberState;

    private boolean isAlarmOn;

    private String firebaseToken;

    @ManyToOne
    @JoinColumn(name = "love_type_id")
    private LoveType loveType;

    private float avoidanceRate;

    private float anxietyRate;

    private String nickname;


}
