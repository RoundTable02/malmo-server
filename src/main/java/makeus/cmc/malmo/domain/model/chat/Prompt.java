package makeus.cmc.malmo.domain.model.chat;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import makeus.cmc.malmo.adaptor.out.persistence.entity.BaseTimeEntity;

import java.time.LocalDateTime;

import static makeus.cmc.malmo.util.GlobalConstants.LAST_PROMPT_LEVEL;
import static makeus.cmc.malmo.util.GlobalConstants.NOT_COUPLE_MEMBER_LAST_PROMPT_LEVEL;

@Getter
@Builder(access = AccessLevel.PRIVATE)
public class Prompt extends BaseTimeEntity {

    private Long id;
    private int level;
    private String content;

    // BaseTimeEntity fields
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;
    private LocalDateTime deletedAt;

    public static Prompt from(Long id, int level, String content,
                                 LocalDateTime createdAt, LocalDateTime modifiedAt, LocalDateTime deletedAt) {
        return Prompt.builder()
                .id(id)
                .level(level)
                .content(content)
                .createdAt(createdAt)
                .modifiedAt(modifiedAt)
                .deletedAt(deletedAt)
                .build();
    }

    public boolean isLastPromptForNotCoupleMember() {
        return level == NOT_COUPLE_MEMBER_LAST_PROMPT_LEVEL;
    }

    public boolean isLastPrompt() {
        return level == LAST_PROMPT_LEVEL;
    }
}
