package makeus.cmc.malmo.domain.model.chat;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.SuperBuilder;
import makeus.cmc.malmo.domain.model.BaseTimeEntity;

@Getter
@SuperBuilder
@AllArgsConstructor
public class ChatRoomSummary extends BaseTimeEntity {
    private Long id;
    private ChatRoom chatRoom;
    private String content;
}