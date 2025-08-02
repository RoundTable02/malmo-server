package makeus.cmc.malmo.application.port.out;

import makeus.cmc.malmo.domain.value.id.InviteCodeValue;

public interface ValidateInviteCodePort {
    boolean isInviteCodeDuplicated(InviteCodeValue inviteCodeValue);
    boolean isAlreadyCoupleMemberByInviteCode(InviteCodeValue inviteCode);
}
