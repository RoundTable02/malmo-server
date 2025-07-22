package makeus.cmc.malmo.adaptor.out.persistence.entity.question;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import makeus.cmc.malmo.adaptor.out.persistence.entity.BaseTimeEntity;
import makeus.cmc.malmo.adaptor.out.persistence.entity.couple.CoupleMemberEntity;
import makeus.cmc.malmo.adaptor.out.persistence.entity.value.CoupleMemberEntityId;
import makeus.cmc.malmo.adaptor.out.persistence.entity.value.CoupleQuestionEntityId;
import makeus.cmc.malmo.domain.value.state.MemberAnswerState;

@Getter
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class MemberAnswerEntity extends BaseTimeEntity {

    @Column(name = "memberAnswerId")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Embedded
    private CoupleQuestionEntityId coupleQuestionEntityId;

    @Embedded
    private CoupleMemberEntityId coupleMemberEntityId;

    private String answer;

    @Enumerated(EnumType.STRING)
    private MemberAnswerState memberAnswerState;

}
