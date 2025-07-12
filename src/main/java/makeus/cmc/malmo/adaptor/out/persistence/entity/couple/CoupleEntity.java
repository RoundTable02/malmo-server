package makeus.cmc.malmo.adaptor.out.persistence.entity.couple;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import makeus.cmc.malmo.adaptor.out.persistence.entity.BaseTimeEntity;
import makeus.cmc.malmo.domain.value.state.CoupleState;

import java.time.LocalDate;
import java.util.List;

@Getter
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class CoupleEntity extends BaseTimeEntity {

    @Column(name = "coupleId")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate startLoveDate;

    @Enumerated(EnumType.STRING)
    private CoupleState coupleState;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "couple_id")
    private List<CoupleMemberEntity> coupleMembers;
}
