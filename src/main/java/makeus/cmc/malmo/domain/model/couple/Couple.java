package makeus.cmc.malmo.domain.model.couple;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import makeus.cmc.malmo.domain.model.BaseTimeEntity;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@Entity
public class Couple extends BaseTimeEntity {

    @Column(name = "coupleId")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String inviteCode;

    private LocalDateTime startLoveDate;

    @Enumerated(EnumType.STRING)
    private CoupleState coupleState;

    private LocalDateTime deletedDate;
}
