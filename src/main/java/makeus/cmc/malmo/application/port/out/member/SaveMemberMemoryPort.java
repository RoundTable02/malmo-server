package makeus.cmc.malmo.application.port.out.member;

import makeus.cmc.malmo.domain.model.member.MemberMemory;
import makeus.cmc.malmo.domain.value.id.MemberId;

public interface SaveMemberMemoryPort {

    void saveMemberMemory(MemberMemory memberMemory);

    void deleteAllMemory(MemberId memberId);
}
