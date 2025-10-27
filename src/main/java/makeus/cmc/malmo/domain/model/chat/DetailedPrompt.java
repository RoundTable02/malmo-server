package makeus.cmc.malmo.domain.model.chat;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import makeus.cmc.malmo.adaptor.out.persistence.entity.BaseTimeEntity;

import java.time.LocalDateTime;

@Getter
@Builder(access = AccessLevel.PRIVATE)
public class DetailedPrompt extends BaseTimeEntity {

    private Long id;
    private int level;
    private int detailedLevel;
    private String content;
    private boolean isForValidation;
    private boolean isForSummary;
    private String metadataTitle;
    private boolean isLastDetailedPrompt;
    private boolean isForGuideline;

    // BaseTimeEntity fields
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;
    private LocalDateTime deletedAt;

    public static DetailedPrompt from(Long id, int level, int detailedLevel, String content,
                                     boolean isForValidation, boolean isForSummary, String metadataTitle,
                                     boolean isLastDetailedPrompt, boolean isForGuideline,
                                     LocalDateTime createdAt, LocalDateTime modifiedAt, LocalDateTime deletedAt) {
        return DetailedPrompt.builder()
                .id(id)
                .level(level)
                .detailedLevel(detailedLevel)
                .content(content)
                .isForValidation(isForValidation)
                .isForSummary(isForSummary)
                .metadataTitle(metadataTitle)
                .isLastDetailedPrompt(isLastDetailedPrompt)
                .isForGuideline(isForGuideline)
                .createdAt(createdAt)
                .modifiedAt(modifiedAt)
                .deletedAt(deletedAt)
                .build();
    }

    public static DetailedPrompt create(int level, int detailedLevel, String content,
                                       boolean isForValidation, boolean isForSummary, String metadataTitle,
                                       boolean isLastDetailedPrompt, boolean isForGuideline) {
        return DetailedPrompt.builder()
                .level(level)
                .detailedLevel(detailedLevel)
                .content(content)
                .isForValidation(isForValidation)
                .isForSummary(isForSummary)
                .metadataTitle(metadataTitle)
                .isLastDetailedPrompt(isLastDetailedPrompt)
                .isForGuideline(isForGuideline)
                .build();
    }
}
