package makeus.cmc.malmo.domain.model.couple;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.SuperBuilder;
import makeus.cmc.malmo.domain.model.BaseTimeEntity;
import makeus.cmc.malmo.domain.value.id.CoupleId;
import makeus.cmc.malmo.domain.value.id.MemberId;
import makeus.cmc.malmo.domain.value.state.CoupleMemberState;

@Getter
@SuperBuilder
@AllArgsConstructor
public class CoupleMember extends BaseTimeEntity {
    private Long id;
    private MemberId memberId;
    private CoupleId coupleId;
    private CoupleMemberState coupleMemberState;
}