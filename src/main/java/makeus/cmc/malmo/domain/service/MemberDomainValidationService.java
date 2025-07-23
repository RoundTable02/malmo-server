package makeus.cmc.malmo.domain.service;

import lombok.RequiredArgsConstructor;
import makeus.cmc.malmo.application.port.out.ValidateMemberPort;
import makeus.cmc.malmo.domain.exception.MemberNotFoundException;
import makeus.cmc.malmo.domain.exception.NotCoupleMemberException;
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

    public void isMemberValid(MemberId memberId) {
        boolean validMember = validateMemberPort.isValidMember(memberId);

        if (!validMember) {
            throw new MemberNotFoundException("존재하지 않는 사용자입니다. 회원가입 후 이용해주세요.");
        }
    }
}
