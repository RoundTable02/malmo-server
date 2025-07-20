package makeus.cmc.malmo.domain.model.chat;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import makeus.cmc.malmo.domain.value.id.ChatRoomId;
import makeus.cmc.malmo.domain.value.type.SenderType;

import java.time.LocalDateTime;

@Getter
@Builder(access = AccessLevel.PRIVATE)
public class ChatMessage {
    private Long id;
    private ChatRoomId chatRoomId;
    private int level;
    private String content;
    private SenderType senderType;

    // BaseTimeEntity fields
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;
    private LocalDateTime deletedAt;

    public static ChatMessage createUserTextMessage(ChatRoomId chatRoomId, int level, String content) {
        return ChatMessage.builder()
                .chatRoomId(chatRoomId)
                .level(level)
                .content(content)
                .senderType(SenderType.USER)
                .build();
    }

    public static ChatMessage createAssistantTextMessage(ChatRoomId chatRoomId, int level, String content) {
        return ChatMessage.builder()
                .chatRoomId(chatRoomId)
                .level(level)
                .content(content)
                .senderType(SenderType.ASSISTANT)
                .build();
    }

    public static ChatMessage from(Long id, ChatRoomId chatRoomId, int level, String content, SenderType senderType, LocalDateTime createdAt, LocalDateTime modifiedAt, LocalDateTime deletedAt) {
        return ChatMessage.builder()
                .id(id)
                .chatRoomId(chatRoomId)
                .level(level)
                .content(content)
                .senderType(senderType)
                .createdAt(createdAt)
                .modifiedAt(modifiedAt)
                .deletedAt(deletedAt)
                .build();
    }

}