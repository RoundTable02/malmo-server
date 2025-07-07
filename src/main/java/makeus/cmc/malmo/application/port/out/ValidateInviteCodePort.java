package makeus.cmc.malmo.application.port.out;

import makeus.cmc.malmo.domain.model.value.InviteCodeValue;

public interface ValidateInviteCodePort {
    boolean validateDuplicateInviteCode(InviteCodeValue inviteCodeValue);
    boolean isAlreadyCoupleMemberByInviteCode(InviteCodeValue inviteCode);
}
