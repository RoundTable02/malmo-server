package makeus.cmc.malmo.domain.model.couple;

import lombok.Getter;
import lombok.experimental.SuperBuilder;
import lombok.AllArgsConstructor;
import makeus.cmc.malmo.domain.model.BaseTimeEntity;
import makeus.cmc.malmo.domain.model.member.Member;

@Getter
@SuperBuilder
@AllArgsConstructor
public class CoupleMember extends BaseTimeEntity {
    private Long id;
    private Member member;
    private Couple couple;
    private CoupleMemberState coupleMemberState;
}