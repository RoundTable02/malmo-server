package makeus.cmc.malmo.domain.model.terms;

import lombok.Getter;
import lombok.experimental.SuperBuilder;
import lombok.AllArgsConstructor;
import makeus.cmc.malmo.domain.model.BaseTimeEntity;
import makeus.cmc.malmo.domain.model.member.Member;

@Getter
@SuperBuilder
@AllArgsConstructor
public class MemberTermsAgreement extends BaseTimeEntity {
    private Long id;
    private Member member;
    private Terms terms;
    private boolean agreed;
}