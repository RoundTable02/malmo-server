package makeus.cmc.malmo.domain.model.chat;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.SuperBuilder;
import makeus.cmc.malmo.domain.model.BaseTimeEntity;
import makeus.cmc.malmo.domain.model.member.Member;
import makeus.cmc.malmo.domain.value.state.SavedChatMessageState;

@Getter
@SuperBuilder
@AllArgsConstructor
public class SavedChatMessage extends BaseTimeEntity {
    private Long id;
    private ChatMessage chatMessage;
    private Member member;
    private SavedChatMessageState savedChatMessageState;
}