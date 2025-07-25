package makeus.cmc.malmo.adaptor.out.persistence.entity.question;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import makeus.cmc.malmo.adaptor.out.persistence.entity.BaseTimeEntity;
import makeus.cmc.malmo.adaptor.out.persistence.entity.value.MemberEntityId;
import makeus.cmc.malmo.domain.value.state.CoupleQuestionState;

@Getter
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class TempCoupleQuestionEntity extends BaseTimeEntity {
    @Column(name = "TempCoupleQuestionId")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "question_id")
    private QuestionEntity question;

    @Embedded
    private MemberEntityId memberId;

    @Column(columnDefinition = "TEXT")
    private String answer;

    @Enumerated(EnumType.STRING)
    private CoupleQuestionState coupleQuestionState;

}