package makeus.cmc.malmo.domain.model.chat;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import makeus.cmc.malmo.domain.value.id.ChatRoomId;
import makeus.cmc.malmo.domain.value.id.MemberId;

import java.time.LocalDateTime;

@Getter
@Builder(access = AccessLevel.PRIVATE)
public class MemberChatRoomMetadata {

    private Long id;
    private ChatRoomId chatRoomId;
    private MemberId memberId;
    private int level;
    private int detailedLevel;
    private String title;
    private String summary;
    private LocalDateTime createdAt;

    public static MemberChatRoomMetadata from(Long id, ChatRoomId chatRoomId, MemberId memberId,
                                             int level, int detailedLevel, String title, String summary,
                                             LocalDateTime createdAt) {
        return MemberChatRoomMetadata.builder()
                .id(id)
                .chatRoomId(chatRoomId)
                .memberId(memberId)
                .level(level)
                .detailedLevel(detailedLevel)
                .title(title)
                .summary(summary)
                .createdAt(createdAt)
                .build();
    }

    public static MemberChatRoomMetadata create(ChatRoomId chatRoomId, MemberId memberId,
                                               int level, int detailedLevel, String title, String summary) {
        return MemberChatRoomMetadata.builder()
                .chatRoomId(chatRoomId)
                .memberId(memberId)
                .level(level)
                .detailedLevel(detailedLevel)
                .title(title)
                .summary(summary)
                .createdAt(LocalDateTime.now())
                .build();
    }
}
