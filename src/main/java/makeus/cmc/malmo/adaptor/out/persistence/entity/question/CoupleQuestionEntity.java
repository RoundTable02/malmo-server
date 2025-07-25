package makeus.cmc.malmo.adaptor.out.persistence.entity.question;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import makeus.cmc.malmo.adaptor.out.persistence.entity.BaseTimeEntity;
import makeus.cmc.malmo.adaptor.out.persistence.entity.value.CoupleEntityId;
import makeus.cmc.malmo.domain.value.state.CoupleQuestionState;

import java.time.LocalDateTime;

@Getter
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class CoupleQuestionEntity extends BaseTimeEntity {

    @Column(name = "coupleQuestionId")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "question_id")
    private QuestionEntity question;

    @Embedded
    private CoupleEntityId coupleEntityId;

    @Enumerated(EnumType.STRING)
    private CoupleQuestionState coupleQuestionState;

    private LocalDateTime bothAnsweredAt;
}
