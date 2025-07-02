package makeus.cmc.malmo.adaptor.out.persistence.entity.couple;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import makeus.cmc.malmo.adaptor.out.persistence.entity.BaseTimeEntityJpa;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class CoupleEntity extends BaseTimeEntityJpa {

    @Column(name = "coupleId")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate startLoveDate;

    @Enumerated(EnumType.STRING)
    private CoupleStateJpa coupleStateJpa;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "couple_id")
    private List<CoupleMemberEntity> coupleMembers;
}
