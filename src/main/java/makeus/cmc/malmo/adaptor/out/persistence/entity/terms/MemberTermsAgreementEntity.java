package makeus.cmc.malmo.adaptor.out.persistence.entity.terms;


import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import makeus.cmc.malmo.adaptor.out.persistence.entity.BaseTimeEntityJpa;
import makeus.cmc.malmo.adaptor.out.persistence.entity.member.MemberEntity;
import makeus.cmc.malmo.adaptor.out.persistence.entity.value.MemberEntityId;
import makeus.cmc.malmo.adaptor.out.persistence.entity.value.TermsEntityId;

@Getter
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class MemberTermsAgreementEntity extends BaseTimeEntityJpa {

    @Column(name = "memberTermsAgreementId")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @AttributeOverride(name = "id", column = @Column(name = "member_id", nullable = false))
    @Embedded
    private MemberEntityId memberEntityId;

    @AttributeOverride(name = "id", column = @Column(name = "member_id", nullable = false))
    @Embedded
    private TermsEntityId termsEntityId;

    private boolean agreed;
}
