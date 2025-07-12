package makeus.cmc.malmo.domain.model.chat;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import makeus.cmc.malmo.domain.model.member.Member;
import makeus.cmc.malmo.domain.value.state.SavedChatMessageState;

import java.time.LocalDateTime;

@Getter
@Builder(access = AccessLevel.PRIVATE)
public class SavedChatMessage {
    private Long id;
    private ChatMessage chatMessage;
    private Member member;
    private SavedChatMessageState savedChatMessageState;

    // BaseTimeEntity fields
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;
    private LocalDateTime deletedAt;
}