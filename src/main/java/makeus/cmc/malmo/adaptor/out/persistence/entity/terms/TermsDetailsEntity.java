package makeus.cmc.malmo.adaptor.out.persistence.entity.terms;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import makeus.cmc.malmo.adaptor.out.persistence.entity.BaseTimeEntity;
import makeus.cmc.malmo.adaptor.out.persistence.entity.value.TermsEntityId;
import makeus.cmc.malmo.domain.value.state.TermsDetailsType;

@Getter
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class TermsDetailsEntity extends BaseTimeEntity {

    @Column(name = "termsDetailsId")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Embedded
    private TermsEntityId termsEntityId;

    @Enumerated(value = EnumType.STRING)
    private TermsDetailsType termsDetailsType;

    @Column(columnDefinition = "TEXT")
    private String content;
}
