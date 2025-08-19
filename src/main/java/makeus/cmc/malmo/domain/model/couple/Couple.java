package makeus.cmc.malmo.domain.model.couple;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import makeus.cmc.malmo.domain.value.id.MemberId;
import makeus.cmc.malmo.domain.value.state.CoupleState;
import makeus.cmc.malmo.domain.value.type.LoveTypeCategory;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

@Getter
@Builder(access = AccessLevel.PRIVATE)
public class Couple {
    private Long id;
    private LocalDate startLoveDate;
    private MemberId firstMemberId;
    private MemberId secondMemberId;
    private CoupleMemberSnapshot firstMemberSnapshot;
    private CoupleMemberSnapshot secondMemberSnapshot;
    private CoupleState coupleState;

    // BaseTimeEntity fields
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;
    private LocalDateTime deletedAt;

    public void recover() {
        this.firstMemberSnapshot = null;
        this.secondMemberSnapshot = null;
        this.coupleState = CoupleState.ALIVE;
    }

    public void delete() {
        this.coupleState = CoupleState.DELETED;
    }

    public boolean isBroken() {
        return Objects.equals(this.coupleState, CoupleState.DELETED);
    }

    public MemberId getOtherMemberId(MemberId memberId) {
        if (Objects.equals(memberId, firstMemberId)) {
            return secondMemberId;
        } else {
            return firstMemberId;
        }
    }

    public void unlink(MemberId memberId, String nickname, LoveTypeCategory loveTypeCategory, float anxietyRate, float avoidanceRate) {
        this.coupleState = CoupleState.DELETED;

        CoupleMemberSnapshot coupleMemberSnapshot = new CoupleMemberSnapshot(memberId, nickname, loveTypeCategory, anxietyRate, avoidanceRate);
        if (Objects.equals(memberId, firstMemberId)) {
            this.firstMemberSnapshot = coupleMemberSnapshot;
        } else {
            this.secondMemberSnapshot = coupleMemberSnapshot;
        }
    }

    public void updateStartLoveDate(LocalDate startLoveDate) {
        this.startLoveDate = startLoveDate;
    }
}