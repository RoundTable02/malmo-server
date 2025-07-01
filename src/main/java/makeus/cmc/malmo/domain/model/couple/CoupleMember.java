package makeus.cmc.malmo.domain.model.couple;

import lombok.Getter;
import lombok.experimental.SuperBuilder;
import lombok.AllArgsConstructor;
import makeus.cmc.malmo.domain.model.BaseTimeEntity;
import makeus.cmc.malmo.domain.model.value.CoupleId;
import makeus.cmc.malmo.domain.model.value.MemberId;

@Getter
@SuperBuilder
@AllArgsConstructor
public class CoupleMember extends BaseTimeEntity {
    private Long id;
    private MemberId memberId;
    private CoupleId coupleId;
    private CoupleMemberState coupleMemberState;
}