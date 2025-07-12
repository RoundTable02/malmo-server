package makeus.cmc.malmo.domain.model.chat;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.SuperBuilder;
import makeus.cmc.malmo.domain.model.BaseTimeEntity;
import makeus.cmc.malmo.domain.value.id.MemberId;
import makeus.cmc.malmo.domain.value.state.ChatRoomState;

@Getter
@SuperBuilder
@AllArgsConstructor
public class ChatRoom extends BaseTimeEntity {
    private Long id;
    private MemberId memberId;
    private ChatRoomState chatRoomState;

    public static ChatRoom createChatRoom(MemberId memberId) {
        return ChatRoom.builder()
                .memberId(memberId)
                .chatRoomState(ChatRoomState.ALIVE)
                .build();
    }
}
