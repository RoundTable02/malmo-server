package makeus.cmc.malmo.application.port.out;

import makeus.cmc.malmo.domain.model.value.InviteCodeValue;
import makeus.cmc.malmo.domain.model.value.MemberId;

import java.util.Optional;

public interface LoadInviteCodePort {
    Optional<InviteCodeValue> loadInviteCodeByMemberId(MemberId memberId);
}
