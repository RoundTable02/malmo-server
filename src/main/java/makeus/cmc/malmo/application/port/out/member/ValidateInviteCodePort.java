package makeus.cmc.malmo.application.port.out.member;

import makeus.cmc.malmo.domain.value.id.InviteCodeValue;
import makeus.cmc.malmo.domain.value.id.MemberId;

public interface ValidateInviteCodePort {
    boolean isInviteCodeDuplicated(InviteCodeValue inviteCodeValue);
}
