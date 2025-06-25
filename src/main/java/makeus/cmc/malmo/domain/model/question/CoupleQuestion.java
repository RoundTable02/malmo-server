package makeus.cmc.malmo.domain.model.question;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import makeus.cmc.malmo.domain.model.BaseTimeEntity;
import makeus.cmc.malmo.domain.model.couple.Couple;

@Getter
@NoArgsConstructor
@Entity
public class CoupleQuestion extends BaseTimeEntity {

    @Column(name = "coupleQuestionId")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "question_id")
    private Question question;

    @ManyToOne
    @JoinColumn(name = "couple_id")
    private Couple couple;

    @Enumerated(EnumType.STRING)
    private CoupleQuestionState coupleQuestionState;
}
