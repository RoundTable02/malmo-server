package makeus.cmc.malmo.adaptor.out.persistence.entity.terms;


import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import makeus.cmc.malmo.adaptor.out.persistence.entity.BaseTimeEntityJpa;
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

    @Embedded
    private MemberEntityId memberEntityId;

    @Embedded
    private TermsEntityId termsEntityId;

    private boolean agreed;
}
