package makeus.cmc.malmo.domain.service;

import lombok.RequiredArgsConstructor;
import makeus.cmc.malmo.adaptor.out.persistence.exception.MemberNotFoundException;
import makeus.cmc.malmo.application.port.out.LoadMemberPort;
import makeus.cmc.malmo.domain.model.member.Member;
import makeus.cmc.malmo.domain.model.value.MemberId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberDomainService {

    private final LoadMemberPort loadMemberPort;

    public Member getMemberById(MemberId memberId) {
        return loadMemberPort.loadMemberById(memberId.getValue())
                .orElseThrow(MemberNotFoundException::new);
    }

}
