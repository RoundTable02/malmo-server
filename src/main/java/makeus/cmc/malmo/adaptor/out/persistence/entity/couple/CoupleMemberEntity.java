package makeus.cmc.malmo.adaptor.out.persistence.entity.couple;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import makeus.cmc.malmo.adaptor.out.persistence.entity.BaseTimeEntityJpa;
import makeus.cmc.malmo.adaptor.out.persistence.entity.value.CoupleEntityId;
import makeus.cmc.malmo.adaptor.out.persistence.entity.value.MemberEntityId;

@Getter
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class CoupleMemberEntity extends BaseTimeEntityJpa {

    @Column(name = "coupleMemberId")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Embedded
    private MemberEntityId memberEntityId;

    @Embedded
    private CoupleEntityId coupleEntityId;

    @Enumerated(EnumType.STRING)
    private CoupleMemberStateJpa coupleMemberStateJpa;
}
