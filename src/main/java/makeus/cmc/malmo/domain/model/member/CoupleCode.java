package makeus.cmc.malmo.domain.model.member;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.SuperBuilder;
import makeus.cmc.malmo.domain.model.BaseTimeEntity;

import java.time.LocalDate;

@Getter
@SuperBuilder
@AllArgsConstructor
public class CoupleCode extends BaseTimeEntity {
    private Long id;
    private String inviteCode;
    private LocalDate startLoveDate;
    private Long memberId;
}
