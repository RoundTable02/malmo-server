package makeus.cmc.malmo.application.helper.member;

import lombok.RequiredArgsConstructor;
import makeus.cmc.malmo.application.port.out.member.SaveMemberMemoryPort;
import makeus.cmc.malmo.domain.model.member.MemberMemory;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MemberMemoryCommandHelper {

    private final SaveMemberMemoryPort saveMemberMemoryPort;

    public void saveMemberMemory(MemberMemory memberMemory) {
        saveMemberMemoryPort.saveMemberMemory(memberMemory);
    }
}
