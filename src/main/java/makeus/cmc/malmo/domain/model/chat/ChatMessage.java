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

    public static ChatMessage createUserTextMessage(ChatRoomId chatRoomId, String content) {
        return ChatMessage.builder()
                .chatRoomId(chatRoomId)
                .isImage(false)
                .imageUrl(null)
                .extractedText(null)
                .content(content)
                .senderType(SenderType.USER)
                .build();
    }

    public static ChatMessage createAssistantTextMessage(ChatRoomId chatRoomId, String content) {
        return ChatMessage.builder()
                .chatRoomId(chatRoomId)
                .isImage(false)
                .imageUrl(null)
                .extractedText(null)
                .content(content)
                .senderType(SenderType.ASSISTANT)
                .build();
    }
}