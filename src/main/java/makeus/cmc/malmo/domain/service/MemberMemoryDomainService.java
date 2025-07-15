package makeus.cmc.malmo.domain.service;

import lombok.RequiredArgsConstructor;
import makeus.cmc.malmo.application.port.out.LoadMemberMemoryPort;
import makeus.cmc.malmo.domain.model.member.MemberMemory;
import makeus.cmc.malmo.domain.value.id.MemberId;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class MemberMemoryDomainService {

    private final LoadMemberMemoryPort loadMemberMemoryPort;

    // 사용자 정보 리스트를 "- "로 구분하여 문자열로 반환
    public String getMemberMemoriesByMemberId(MemberId memberId) {
        StringBuilder sb = new StringBuilder();
        List<MemberMemory> memoryList = loadMemberMemoryPort.loadMemberMemoryByMemberId(memberId);

        for (MemberMemory memberMemory : memoryList) {
            sb.append("- ").append(memberMemory.getContent()).append("\n");
        }

        return sb.toString();
    }
}
