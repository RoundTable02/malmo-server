package makeus.cmc.malmo.domain.model.member;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder(access = lombok.AccessLevel.PRIVATE)
public class MemberMemory {
    private Long id;
    private Member member;
    private String content;

    // BaseTimeEntity fields
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;
    private LocalDateTime deletedAt;
}