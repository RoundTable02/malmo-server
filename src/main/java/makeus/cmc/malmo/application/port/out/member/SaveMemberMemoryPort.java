package makeus.cmc.malmo.application.port.out.member;

import makeus.cmc.malmo.domain.model.member.MemberMemory;
import makeus.cmc.malmo.domain.value.id.CoupleMemberId;
import makeus.cmc.malmo.domain.value.id.MemberId;

public interface SaveMemberMemoryPort {

    void saveMemberMemory(MemberMemory memberMemory);

    void deleteAliveMemory(MemberId memberId);

    void recoverMemory(CoupleMemberId coupleMemberId);
}
