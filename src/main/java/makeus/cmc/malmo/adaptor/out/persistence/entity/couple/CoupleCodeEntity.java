package makeus.cmc.malmo.adaptor.out.persistence.entity.couple;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import makeus.cmc.malmo.adaptor.out.persistence.entity.BaseTimeEntityJpa;
import makeus.cmc.malmo.adaptor.out.persistence.entity.value.MemberEntityId;

import java.time.LocalDate;

@Getter
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class CoupleCodeEntity extends BaseTimeEntityJpa {
    @Column(name = "coupleCodeId")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String inviteCode;

    private LocalDate startLoveDate;

    @Enumerated(EnumType.STRING)
    private CoupleCodeStateJpa coupleCodeStateJpa;

    @AttributeOverride(name = "id", column = @Column(name = "member_id", nullable = false))
    @Embedded
    private MemberEntityId memberEntityId;
}
