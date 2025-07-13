package makeus.cmc.malmo.domain.service;

import lombok.RequiredArgsConstructor;
import makeus.cmc.malmo.application.port.out.LoadMemberPort;
import makeus.cmc.malmo.domain.exception.MemberNotFoundException;
import makeus.cmc.malmo.domain.model.member.Member;
import makeus.cmc.malmo.domain.value.id.InviteCodeValue;
import makeus.cmc.malmo.domain.value.id.MemberId;
import makeus.cmc.malmo.domain.value.state.MemberState;
import makeus.cmc.malmo.domain.value.type.MemberRole;
import makeus.cmc.malmo.domain.value.type.Provider;
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

    public Member createMember(Provider provider, String providerId, String email, InviteCodeValue inviteCode) {
        return Member.createMember(
                provider,
                providerId,
                MemberRole.MEMBER,
                MemberState.BEFORE_ONBOARDING,
                email,
                inviteCode
        );
    }

}
