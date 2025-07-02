package makeus.cmc.malmo.domain.model.couple;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.SuperBuilder;
import makeus.cmc.malmo.domain.model.BaseTimeEntity;
import makeus.cmc.malmo.domain.model.value.MemberId;

import java.time.LocalDate;
import java.util.List;

@Getter
@SuperBuilder
@AllArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class Couple extends BaseTimeEntity {
    private Long id;
    private LocalDate startLoveDate;
    private CoupleState coupleState;
    private List<CoupleMember> coupleMembers;

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
}