package makeus.cmc.malmo.application.port.out;

import makeus.cmc.malmo.domain.model.member.CoupleCode;

import java.util.Optional;

public interface LoadCoupleCodePort {
    Optional<CoupleCode> loadCoupleCodeByInviteCode(String inviteCode);
}
