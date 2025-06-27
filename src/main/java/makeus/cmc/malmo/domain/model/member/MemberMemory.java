package makeus.cmc.malmo.domain.model.member;

import lombok.Getter;
import lombok.experimental.SuperBuilder;
import lombok.AllArgsConstructor;
import makeus.cmc.malmo.domain.model.BaseTimeEntity;

@Getter
@SuperBuilder
@AllArgsConstructor
public class MemberMemory extends BaseTimeEntity {
    private Long id;
    private Member member;
    private String content;
}