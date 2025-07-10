package makeus.cmc.malmo.domain.model.chat;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.SuperBuilder;
import makeus.cmc.malmo.domain.model.BaseTimeEntity;
import makeus.cmc.malmo.domain.model.value.ChatRoomId;

@Getter
@SuperBuilder
@AllArgsConstructor
public class ChatMessage extends BaseTimeEntity {
    private Long id;
    private ChatRoomId chatRoomId;
    private boolean isImage;
    private String imageUrl;
    private String extractedText;
    private String content;
    private SenderType senderType;
}