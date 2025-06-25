package makeus.cmc.malmo.domain.model.terms;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import makeus.cmc.malmo.domain.model.BaseTimeEntity;

@Getter
@NoArgsConstructor
@Entity
public class Terms extends BaseTimeEntity {

    @Column(name = "termsId")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private String content;

    private String version;

    private boolean isRequired;

}
