package makeus.cmc.malmo.domain.model.chat;

import lombok.Getter;
import lombok.experimental.SuperBuilder;
import lombok.AllArgsConstructor;
import makeus.cmc.malmo.domain.model.BaseTimeEntity;
import makeus.cmc.malmo.domain.model.member.Member;

@Getter
@SuperBuilder
@AllArgsConstructor
public class SavedChatMessage extends BaseTimeEntity {
    private Long id;
    private ChatMessage chatMessage;
    private Member member;
    private SavedChatMessageState savedChatMessageState;
}