package makeus.cmc.malmo.domain.model.chat;

import jakarta.persistence.Embedded;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.SuperBuilder;
import makeus.cmc.malmo.domain.model.BaseTimeEntity;
import makeus.cmc.malmo.domain.model.member.Member;
import makeus.cmc.malmo.domain.model.value.ChatRoomId;
import makeus.cmc.malmo.domain.model.value.MemberId;

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

    public ChatMessage createTextChatMessage(SenderType senderType, String content) {
        return ChatMessage.builder()
                .chatRoomId(ChatRoomId.of(this.id))
                .isImage(false)
                .imageUrl(null)
                .extractedText(null)
                .content(content)
                .senderType(senderType)
                .build();
    }
}
