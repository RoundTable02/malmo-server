package makeus.cmc.malmo.application.port.out.sse;

import makeus.cmc.malmo.domain.value.id.MemberId;

public interface ValidateSsePort {
    boolean isMemberOnline(MemberId memberId);
}
