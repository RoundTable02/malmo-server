package makeus.cmc.malmo.domain.model.member;

import lombok.Builder;
import lombok.Getter;
import makeus.cmc.malmo.domain.value.id.MemberId;
import makeus.cmc.malmo.domain.value.state.MemberMemoryState;

import java.time.LocalDateTime;

@Getter
@Builder(access = lombok.AccessLevel.PRIVATE)
public class MemberMemory {
    private Long id;
    private MemberId memberId;
    private String content;
    private MemberMemoryState memberMemoryState;

    // BaseTimeEntity fields
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;
    private LocalDateTime deletedAt;

    public static MemberMemory from(Long id, MemberId memberId, String content, MemberMemoryState memberMemoryState,
                                  LocalDateTime createdAt, LocalDateTime modifiedAt, LocalDateTime deletedAt) {
        return MemberMemory.builder()
                .id(id)
                .memberId(memberId)
                .content(content)
                .memberMemoryState(memberMemoryState)
                .createdAt(createdAt)
                .modifiedAt(modifiedAt)
                .deletedAt(deletedAt)
                .build();
    }

    public static MemberMemory createMemberMemory(MemberId memberId, String content) {
        return MemberMemory.builder()
                .memberId(memberId)
                .content(content)
                .memberMemoryState(MemberMemoryState.ALIVE)
                .build();
    }
}