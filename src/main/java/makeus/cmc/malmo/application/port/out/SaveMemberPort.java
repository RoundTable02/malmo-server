package makeus.cmc.malmo.application.port.out;

import makeus.cmc.malmo.domain.model.member.Member;

public interface SaveMemberPort {
    Member saveMember(Member member);
}
