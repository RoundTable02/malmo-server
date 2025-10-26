package makeus.cmc.malmo.domain.model.chat;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import makeus.cmc.malmo.adaptor.out.persistence.entity.BaseTimeEntity;

import java.time.LocalDateTime;


@Getter
@Builder(access = AccessLevel.PRIVATE)
public class Prompt extends BaseTimeEntity {

    private Long id;
    private int level;
    private String content;
    private boolean isForSystem;
    private boolean isForSummary;
    private boolean isForCompletedResponse;
    private boolean isForTotalSummary;
    private boolean isForGuideline;
    private boolean isForAnswerMetadata;

    // BaseTimeEntity fields
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;
    private LocalDateTime deletedAt;

    public static Prompt from(Long id, int level, String content,
                             boolean isForSystem, boolean isForSummary, boolean isForCompletedResponse,
                             boolean isForTotalSummary, boolean isForGuideline, boolean isForAnswerMetadata,
                             LocalDateTime createdAt, LocalDateTime modifiedAt, LocalDateTime deletedAt) {
        return Prompt.builder()
                .id(id)
                .level(level)
                .content(content)
                .isForSystem(isForSystem)
                .isForSummary(isForSummary)
                .isForCompletedResponse(isForCompletedResponse)
                .isForTotalSummary(isForTotalSummary)
                .isForGuideline(isForGuideline)
                .isForAnswerMetadata(isForAnswerMetadata)
                .createdAt(createdAt)
                .modifiedAt(modifiedAt)
                .deletedAt(deletedAt)
                .build();
    }
}
