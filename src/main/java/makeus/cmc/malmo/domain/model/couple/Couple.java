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
    private Boolean isStartLoveDateUpdated;

    // BaseTimeEntity fields
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;
    private LocalDateTime deletedAt;

    public static Couple createCouple(Long memberId, Long partnerId, LocalDate startLoveDate, CoupleState coupleState) {
        return Couple.builder()
                .firstMemberId(MemberId.of(memberId))
                .secondMemberId(MemberId.of(partnerId))
                .startLoveDate(startLoveDate)
                .coupleState(coupleState)
                .isStartLoveDateUpdated(false)
                .build();
    }

    public static Couple from(Long id, LocalDate startLoveDate,
                              MemberId firstMemberId, MemberId secondMemberId,
                              CoupleState coupleState,
                          CoupleMemberSnapshot firstMemberSnapshot, CoupleMemberSnapshot secondMemberSnapshot,
                          LocalDateTime createdAt, LocalDateTime modifiedAt, LocalDateTime deletedAt, Boolean isStartLoveDateUpdated) {
        return Couple.builder()
                .id(id)
                .startLoveDate(startLoveDate)
                .firstMemberId(firstMemberId)
                .secondMemberId(secondMemberId)
                .firstMemberSnapshot(firstMemberSnapshot)
                .secondMemberSnapshot(secondMemberSnapshot)
                .coupleState(coupleState)
                .isStartLoveDateUpdated(isStartLoveDateUpdated)
                .createdAt(createdAt)
                .modifiedAt(modifiedAt)
                .deletedAt(deletedAt)
                .build();
    }

    public void recover() {
        this.firstMemberSnapshot = null;
        this.secondMemberSnapshot = null;
        this.coupleState = CoupleState.ALIVE;
        this.deletedAt = null;
    }

    public boolean canRecover() {
        if (this.deletedAt == null) {
            return false;
        }
        return this.deletedAt.isAfter(LocalDateTime.now().minusDays(30));
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
        this.deletedAt = LocalDateTime.now();

        CoupleMemberSnapshot coupleMemberSnapshot = new CoupleMemberSnapshot(nickname, loveTypeCategory, anxietyRate, avoidanceRate);
        if (Objects.equals(memberId, firstMemberId)) {
            this.firstMemberSnapshot = coupleMemberSnapshot;
        } else {
            this.secondMemberSnapshot = coupleMemberSnapshot;
        }
    }

    public void updateStartLoveDate(LocalDate startLoveDate) {
        this.startLoveDate = startLoveDate;
        this.isStartLoveDateUpdated = true;
    }
}