package makeus.cmc.malmo.domain.model.terms;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import makeus.cmc.malmo.domain.model.BaseTimeEntity;
import makeus.cmc.malmo.domain.model.member.Member;

@Getter
@NoArgsConstructor
@Entity
public class MemberTermsAgreement extends BaseTimeEntity {

    @Column(name = "memberTermsAgreementId")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne
    @JoinColumn(name = "terms_id")
    private Terms terms;

    private boolean agreed;
}
