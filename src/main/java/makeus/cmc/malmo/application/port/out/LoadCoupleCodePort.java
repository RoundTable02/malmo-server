package makeus.cmc.malmo.application.port.out;

import makeus.cmc.malmo.domain.model.member.CoupleCode;
import makeus.cmc.malmo.domain.model.value.MemberId;

import java.util.Optional;

public interface LoadCoupleCodePort {
    Optional<CoupleCode> loadCoupleCodeByInviteCode(String inviteCode);
    Optional<CoupleCode> loadCoupleCodeByMemberId(MemberId memberId);
}
