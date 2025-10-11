package makeus.cmc.malmo.domain.model.chat;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import makeus.cmc.malmo.domain.value.id.MemberId;
import makeus.cmc.malmo.domain.value.state.ChatRoomCompletedReason;
import makeus.cmc.malmo.domain.value.state.ChatRoomState;

import java.time.LocalDateTime;
import java.util.Objects;

import static makeus.cmc.malmo.util.GlobalConstants.*;

@Getter
@Builder(access = AccessLevel.PRIVATE)
public class ChatRoom {
    private Long id;
    private MemberId memberId;
    private ChatRoomState chatRoomState;
    private int level;
    private LocalDateTime lastMessageSentTime;
    private String totalSummary;
    private String situationKeyword;
    private String solutionKeyword;
    private ChatRoomCompletedReason chatRoomCompletedReason;

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
                                int level, LocalDateTime lastMessageSentTime,
                                String totalSummary, String situationKeyword, String solutionKeyword,
                                ChatRoomCompletedReason chatRoomCompletedReason,
                                LocalDateTime createdAt, LocalDateTime modifiedAt, LocalDateTime deletedAt) {
        return ChatRoom.builder()
                .id(id)
                .memberId(memberId)
                .chatRoomState(chatRoomState)
                .level(level)
                .lastMessageSentTime(lastMessageSentTime)
                .totalSummary(totalSummary)
                .situationKeyword(situationKeyword)
                .solutionKeyword(solutionKeyword)
                .chatRoomCompletedReason(chatRoomCompletedReason)
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

    public void updateChatRoomSummary(String totalSummary, String situationKeyword, String solutionKeyword) {
        this.totalSummary = totalSummary;
        this.situationKeyword = situationKeyword;
        this.solutionKeyword = solutionKeyword;
    }

    public void updateLastMessageSentTime() {
        this.lastMessageSentTime = LocalDateTime.now();
    }

    public void completeByUser() {
        this.chatRoomState = ChatRoomState.COMPLETED;
        this.totalSummary = COMPLETED_ROOM_CREATING_SUMMARY_LINE;

        if (this.level == LAST_PROMPT_LEVEL) {
            // 사용자가 마지막 단계에서 종료한 경우
            this.chatRoomCompletedReason = ChatRoomCompletedReason.CHAT_PROCESS_DONE;
        } else {
            // 사용자가 중간 단계에서 종료한 경우
            this.chatRoomCompletedReason = ChatRoomCompletedReason.COMPLETED_BY_USER;
        }
    }

    public boolean isChatRoomValid() {
        return this.chatRoomState == ChatRoomState.ALIVE || this.chatRoomState == ChatRoomState.BEFORE_INIT;
    }

    public void expire() {
        this.chatRoomState = ChatRoomState.COMPLETED;
        this.totalSummary = EXPIRED_ROOM_CREATING_SUMMARY_LINE;
        this.chatRoomCompletedReason = ChatRoomCompletedReason.EXPIRED;
    }

    public boolean isStarted() {
        return this.chatRoomState != ChatRoomState.BEFORE_INIT;
    }

    public boolean isOwner(MemberId memberId) {
        return Objects.equals(this.memberId.getValue(), memberId.getValue());
    }
}
