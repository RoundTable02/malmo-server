package makeus.cmc.malmo.domain.model.chat;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import makeus.cmc.malmo.domain.value.id.MemberId;
import makeus.cmc.malmo.domain.value.state.ChatRoomCompletedReason;
import makeus.cmc.malmo.domain.value.state.ChatRoomState;

import java.time.LocalDateTime;
import java.util.Objects;

import static makeus.cmc.malmo.util.GlobalConstants.INIT_CHATROOM_LEVEL;
import static makeus.cmc.malmo.util.GlobalConstants.COMPLETED_ROOM_CREATING_SUMMARY_LINE;
import static makeus.cmc.malmo.util.GlobalConstants.EXPIRED_ROOM_CREATING_SUMMARY_LINE;

@Getter
@Builder(access = AccessLevel.PRIVATE)
public class ChatRoom {
    private Long id;
    private MemberId memberId;
    private ChatRoomState chatRoomState;
    private int level;
    private int detailedLevel;
    private LocalDateTime lastMessageSentTime;
    private String totalSummary;
    private String situationKeyword;
    private String solutionKeyword;
    private ChatRoomCompletedReason chatRoomCompletedReason;
    private String counselingType;

    // BaseTimeEntity fields
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;
    private LocalDateTime deletedAt;

    public static ChatRoom createChatRoom(MemberId memberId) {
        return ChatRoom.builder()
                .memberId(memberId)
                .level(INIT_CHATROOM_LEVEL)
                .detailedLevel(1)
                .chatRoomState(ChatRoomState.BEFORE_INIT)
                .lastMessageSentTime(LocalDateTime.now())
                .build();
    }

    public static ChatRoom from(Long id, MemberId memberId, ChatRoomState chatRoomState,
                                int level, int detailedLevel, LocalDateTime lastMessageSentTime,
                                String totalSummary, String situationKeyword, String solutionKeyword,
                                ChatRoomCompletedReason chatRoomCompletedReason, String counselingType,
                                LocalDateTime createdAt, LocalDateTime modifiedAt, LocalDateTime deletedAt) {
        return ChatRoom.builder()
                .id(id)
                .memberId(memberId)
                .chatRoomState(chatRoomState)
                .level(level)
                .detailedLevel(detailedLevel)
                .lastMessageSentTime(lastMessageSentTime)
                .totalSummary(totalSummary)
                .situationKeyword(situationKeyword)
                .solutionKeyword(solutionKeyword)
                .chatRoomCompletedReason(chatRoomCompletedReason)
                .counselingType(counselingType)
                .createdAt(createdAt)
                .modifiedAt(modifiedAt)
                .deletedAt(deletedAt)
                .build();
    }

    public void upgradeDetailedLevel() {
        this.detailedLevel += 1;
    }

    public void upgradeToNextStage() {
        this.level += 1;
        this.detailedLevel = 1;
    }

    public void updateChatRoomStateAlive() {
        this.chatRoomState = ChatRoomState.ALIVE;
    }

    public void updateChatRoomSummary(String totalSummary, String situationKeyword, String solutionKeyword, String counselingType) {
        this.totalSummary = totalSummary;
        this.situationKeyword = situationKeyword;
        this.solutionKeyword = solutionKeyword;
        this.counselingType = counselingType;
    }

    public void updateLastMessageSentTime() {
        this.lastMessageSentTime = LocalDateTime.now();
    }

    public void completeByUser() {
        this.chatRoomState = ChatRoomState.COMPLETED;
        this.totalSummary = COMPLETED_ROOM_CREATING_SUMMARY_LINE;
        this.chatRoomCompletedReason = ChatRoomCompletedReason.COMPLETED_BY_USER;
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
