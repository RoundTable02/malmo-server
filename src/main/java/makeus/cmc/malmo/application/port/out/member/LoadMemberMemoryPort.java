package makeus.cmc.malmo.application.port.out.member;

import makeus.cmc.malmo.domain.model.member.MemberMemory;
import makeus.cmc.malmo.domain.value.id.MemberId;

import java.util.List;

public interface LoadMemberMemoryPort {

    List<MemberMemory> loadMemberMemoryByMemberId(MemberId memberId);
}
