package makeus.cmc.malmo.domain.model.couple;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import makeus.cmc.malmo.domain.value.id.CoupleId;
import makeus.cmc.malmo.domain.value.id.MemberId;
import makeus.cmc.malmo.domain.value.state.CoupleMemberState;

import java.time.LocalDateTime;

@Getter
@Builder(access = AccessLevel.PACKAGE)
public class CoupleMember {
    private Long id;
    private MemberId memberId;
    private CoupleId coupleId;
    private CoupleMemberState coupleMemberState;

    // BaseTimeEntity fields
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;
    private LocalDateTime deletedAt;

    public static CoupleMember from(Long id, MemberId memberId, CoupleId coupleId, CoupleMemberState coupleMemberState,
                                   LocalDateTime createdAt, LocalDateTime modifiedAt, LocalDateTime deletedAt) {
        return CoupleMember.builder()
                .id(id)
                .memberId(memberId)
                .coupleId(coupleId)
                .coupleMemberState(coupleMemberState)
                .createdAt(createdAt)
                .modifiedAt(modifiedAt)
                .deletedAt(deletedAt)
                .build();
    }

    public void coupleDeleted() {
        this.coupleMemberState = CoupleMemberState.DELETED;
        this.deletedAt = LocalDateTime.now();
    }
}