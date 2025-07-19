package makeus.cmc.malmo.domain.model.chat;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import makeus.cmc.malmo.domain.value.id.MemberId;
import makeus.cmc.malmo.domain.value.state.ChatRoomState;

import java.time.LocalDateTime;

import static makeus.cmc.malmo.domain.model.chat.ChatRoomConstant.INIT_CHATROOM_LEVEL;

@Getter
@Builder(access = AccessLevel.PRIVATE)
public class ChatRoom {
    private Long id;
    private MemberId memberId;
    private ChatRoomState chatRoomState;
    private int level;
    private LocalDateTime lastMessageSentTime;
    private String totalSummary;

    // BaseTimeEntity fields
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;
    private LocalDateTime deletedAt;

    public static ChatRoom createChatRoom(MemberId memberId) {
        return ChatRoom.builder()
                .memberId(memberId)
                .level(INIT_CHATROOM_LEVEL)
                .chatRoomState(ChatRoomState.BEFORE_INIT)
                .lastMessageSentTime(LocalDateTime.now())
                .build();
    }

    public static ChatRoom from(Long id, MemberId memberId, ChatRoomState chatRoomState,
                                int level, LocalDateTime lastMessageSentTime, String totalSummary,
                                LocalDateTime createdAt, LocalDateTime modifiedAt, LocalDateTime deletedAt) {
        return ChatRoom.builder()
                .id(id)
                .memberId(memberId)
                .chatRoomState(chatRoomState)
                .level(level)
                .lastMessageSentTime(lastMessageSentTime)
                .totalSummary(totalSummary)
                .createdAt(createdAt)
                .modifiedAt(modifiedAt)
                .deletedAt(deletedAt)
                .build();
    }

    public void updateChatRoomStatePaused() {
        this.chatRoomState = ChatRoomState.PAUSED;
    }

    public void upgradeChatRoom() {
        this.level += 1;
        this.chatRoomState = ChatRoomState.NEED_NEXT_QUESTION;
    }

    public void updateChatRoomStateNeedNextQuestion() {
        this.chatRoomState = ChatRoomState.NEED_NEXT_QUESTION;
    }

    public void updateChatRoomStateAlive() {
        this.chatRoomState = ChatRoomState.ALIVE;
    }

    public void updateChatRoomSummary(String summary) {
        this.totalSummary = summary;
    }

    public void complete() {
        this.chatRoomState = ChatRoomState.COMPLETED;
    }

    public boolean isChatRoomValid() {
        return this.chatRoomState == ChatRoomState.ALIVE || this.chatRoomState == ChatRoomState.BEFORE_INIT;
    }
}
