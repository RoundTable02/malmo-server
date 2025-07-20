package makeus.cmc.malmo.domain.service;

import lombok.RequiredArgsConstructor;
import makeus.cmc.malmo.application.port.out.ValidateMemberPort;
import makeus.cmc.malmo.domain.NotCoupleMemberException;
import makeus.cmc.malmo.domain.value.id.MemberId;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MemberDomainValidationService {

    private final ValidateMemberPort validateMemberPort;

    public void isMemberCouple(MemberId memberId) {
        boolean coupleMember = validateMemberPort.isCoupleMember(memberId);

        if (!coupleMember) {
            throw new NotCoupleMemberException("커플 등록 전인 사용자입니다. 커플 등록 후 이용해주세요.");
        }
    }

    public void isMemberTested(MemberId memberId) {
        boolean testedMember = validateMemberPort.isTestedMember(memberId);

        if (!testedMember) {
            throw new NotCoupleMemberException("테스트를 완료하지 않은 사용자입니다. 테스트 후 이용해주세요.");
        }
    }
}
