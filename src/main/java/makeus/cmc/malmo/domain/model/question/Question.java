package makeus.cmc.malmo.domain.model.question;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import makeus.cmc.malmo.domain.model.BaseTimeEntity;

@Getter
@NoArgsConstructor
@Entity
public class Question extends BaseTimeEntity {

    @Column(name = "questionId")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private String content;
}
