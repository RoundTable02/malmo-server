package makeus.cmc.malmo.adaptor.out.persistence.entity.love_type;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import makeus.cmc.malmo.domain.model.BaseTimeEntity;
import makeus.cmc.malmo.domain.value.type.LoveTypeQuestionType;

@Getter
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class LoveTypeQuestionEntity extends BaseTimeEntity {

    @Column(name = "loveTypeQuestionId")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int questionNumber;

    private boolean isReversed;

    private String content;

    @Enumerated(EnumType.STRING)
    private LoveTypeQuestionType loveTypeQuestionType;

    private int weight;
}
