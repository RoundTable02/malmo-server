package makeus.cmc.malmo.domain.model.member;

import lombok.Builder;
import lombok.Getter;
import makeus.cmc.malmo.domain.value.id.CoupleMemberId;
import makeus.cmc.malmo.domain.value.id.MemberId;
import makeus.cmc.malmo.domain.value.state.MemberMemoryState;

import java.time.LocalDateTime;

@Getter
@Builder(access = lombok.AccessLevel.PRIVATE)
public class MemberMemory {
    private Long id;
    private CoupleMemberId coupleMemberId;
    private String content;
    private MemberMemoryState memberMemoryState;

    // BaseTimeEntity fields
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;
    private LocalDateTime deletedAt;

    public static MemberMemory from(Long id, CoupleMemberId coupleMemberId,
                                    String content, MemberMemoryState memberMemoryState,
                                  LocalDateTime createdAt, LocalDateTime modifiedAt, LocalDateTime deletedAt) {
        return MemberMemory.builder()
                .id(id)
                .coupleMemberId(coupleMemberId)
                .content(content)
                .memberMemoryState(memberMemoryState)
                .createdAt(createdAt)
                .modifiedAt(modifiedAt)
                .deletedAt(deletedAt)
                .build();
    }

    public static MemberMemory createMemberMemory(CoupleMemberId coupleMemberId, String content) {
        return MemberMemory.builder()
                .coupleMemberId(coupleMemberId)
                .content(content)
                .memberMemoryState(MemberMemoryState.ALIVE)
                .build();
    }
}