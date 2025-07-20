package makeus.cmc.malmo.domain.model.chat;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import makeus.cmc.malmo.domain.value.id.ChatRoomId;

import java.time.LocalDateTime;

@Getter
@Builder(access = AccessLevel.PRIVATE)
public class ChatMessageSummary {
    private Long id;
    private ChatRoomId chatRoomId;
    private String content;
    private int level;

    // BaseTimeEntity fields
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;
    private LocalDateTime deletedAt;

    public static ChatMessageSummary from(Long id, ChatRoomId chatRoomId, String content, int level,
                                          LocalDateTime createdAt, LocalDateTime modifiedAt, LocalDateTime deletedAt) {
        return ChatMessageSummary.builder()
                .id(id)
                .chatRoomId(chatRoomId)
                .content(content)
                .level(level)
                .createdAt(createdAt)
                .modifiedAt(modifiedAt)
                .deletedAt(deletedAt)
                .build();
    }

    public static ChatMessageSummary createChatMessageSummary(ChatRoomId chatRoomId, String content, int level) {
        return ChatMessageSummary.builder()
                .chatRoomId(chatRoomId)
                .content(content)
                .level(level)
                .build();
    }
}
