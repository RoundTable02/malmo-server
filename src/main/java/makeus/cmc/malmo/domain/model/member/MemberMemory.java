package makeus.cmc.malmo.domain.model.member;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.SuperBuilder;
import makeus.cmc.malmo.domain.model.BaseTimeEntity;

@Getter
@SuperBuilder
@AllArgsConstructor
public class MemberMemory extends BaseTimeEntity {
    private Long id;
    private Member member;
    private String content;
}