package makeus.cmc.malmo.domain.model.question;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import makeus.cmc.malmo.domain.model.BaseTimeEntity;
import makeus.cmc.malmo.domain.model.member.Member;

@Getter
@NoArgsConstructor
@Entity
public class MemberAnswer extends BaseTimeEntity {

    @Column(name = "memberAnswerId")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "couple_question_id")
    private CoupleQuestion coupleQuestion;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

    private String answer;

    @Enumerated(EnumType.STRING)
    private MemberAnswerState memberAnswerState;

}
