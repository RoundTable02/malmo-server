package makeus.cmc.malmo.domain.model.couple;

import lombok.Getter;
import lombok.experimental.SuperBuilder;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import makeus.cmc.malmo.domain.model.BaseTimeEntity;

import java.time.LocalDateTime;

@Getter
@SuperBuilder
@AllArgsConstructor
public class Couple extends BaseTimeEntity {
    private Long id;
    private String inviteCode;
    private LocalDateTime startLoveDate;
    private CoupleState coupleState;
    private LocalDateTime deletedDate;
}