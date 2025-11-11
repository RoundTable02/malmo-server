package makeus.cmc.malmo.adaptor.out.persistence.entity.chat;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import makeus.cmc.malmo.adaptor.out.persistence.entity.BaseTimeEntity;

@Getter
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "detailed_prompt")
public class DetailedPromptEntity extends BaseTimeEntity {

    @Column(name = "detailedPromptId")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int level;

    private int detailedLevel;

    @Column(columnDefinition = "TEXT")
    private String content;

    private boolean isForValidation;

    private boolean isForSummary;

    private String metadataTitle;

    private boolean isLastDetailedPrompt;

    private boolean isForGuideline;
}
