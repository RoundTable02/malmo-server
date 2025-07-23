package makeus.cmc.malmo.domain.model.couple;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import makeus.cmc.malmo.domain.value.id.MemberId;
import makeus.cmc.malmo.domain.value.state.CoupleMemberState;
import makeus.cmc.malmo.domain.value.state.CoupleState;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder(access = AccessLevel.PRIVATE)
public class Couple {
    private Long id;
    private LocalDate startLoveDate;
    private CoupleState coupleState;
    private List<CoupleMember> coupleMembers;

    // BaseTimeEntity fields
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;
    private LocalDateTime deletedAt;

    public static Couple createCouple(Long memberId, Long partnerId, LocalDate startLoveDate, CoupleState coupleState) {
        Couple couple = Couple.builder()
                .startLoveDate(startLoveDate)
                .coupleState(coupleState)
                .build();

        CoupleMember coupleMember = CoupleMember.builder()
                .memberId(MemberId.of(memberId))
                .coupleMemberState(CoupleMemberState.ALIVE)
                .build();

        CoupleMember couplePartner = CoupleMember.builder()
                .memberId(MemberId.of(partnerId))
                .coupleMemberState(CoupleMemberState.ALIVE)
                .build();

        couple.coupleMembers = List.of(coupleMember, couplePartner);

        return couple;
    }

    public static Couple from(Long id, LocalDate startLoveDate, CoupleState coupleState, List<CoupleMember> coupleMembers,
                              LocalDateTime createdAt, LocalDateTime modifiedAt, LocalDateTime deletedAt) {
        return Couple.builder()
                .id(id)
                .startLoveDate(startLoveDate)
                .coupleState(coupleState)
                .coupleMembers(coupleMembers)
                .createdAt(createdAt)
                .modifiedAt(modifiedAt)
                .deletedAt(deletedAt)
                .build();
    }

    public void updateStartLoveDate(LocalDate startLoveDate) {
        this.startLoveDate = startLoveDate;
    }
}