package makeus.cmc.malmo.application.port.out.member;

import makeus.cmc.malmo.domain.model.member.MemberMemory;
import makeus.cmc.malmo.domain.value.id.CoupleId;
import makeus.cmc.malmo.domain.value.id.CoupleMemberId;
import makeus.cmc.malmo.domain.value.id.MemberId;

public interface SaveMemberMemoryPort {

    void saveMemberMemory(MemberMemory memberMemory);

    void deleteAliveMemory(CoupleId coupleId, MemberId memberId);

    void recoverMemory(CoupleId coupleId);
}
