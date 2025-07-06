package makeus.cmc.malmo.adaptor.out.persistence;

import lombok.RequiredArgsConstructor;
import makeus.cmc.malmo.adaptor.out.persistence.repository.MemberRepository;
import makeus.cmc.malmo.application.port.out.ValidateMemberPort;
import makeus.cmc.malmo.domain.model.value.MemberId;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ValidateMemberAdapter implements ValidateMemberPort {

    private final MemberRepository memberRepository;

    @Override
    public boolean isCoupleMember(MemberId memberId) {
        return memberRepository.isCoupleMember(memberId.getValue());
    }
}
