package makeus.cmc.malmo.application.port.out;

import makeus.cmc.malmo.domain.value.id.InviteCodeValue;
import makeus.cmc.malmo.domain.value.id.MemberId;

import java.util.Optional;

public interface LoadInviteCodePort {
    Optional<InviteCodeValue> loadInviteCodeByMemberId(MemberId memberId);
}
