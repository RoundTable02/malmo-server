package makeus.cmc.malmo.domain.service;

import lombok.RequiredArgsConstructor;
import makeus.cmc.malmo.adaptor.out.persistence.exception.MemberNotFoundException;
import makeus.cmc.malmo.application.port.out.LoadMemberPort;
import makeus.cmc.malmo.domain.model.member.Member;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberDomainService {

    private final LoadMemberPort loadMemberPort;

    public Member getMemberById(Long memberId) {
        return loadMemberPort.loadMemberById(memberId)
                .orElseThrow(MemberNotFoundException::new);
    }

}
