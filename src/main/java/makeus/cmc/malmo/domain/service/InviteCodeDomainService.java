package makeus.cmc.malmo.domain.service;

import lombok.RequiredArgsConstructor;
import makeus.cmc.malmo.application.port.out.GenerateInviteCodePort;
import makeus.cmc.malmo.application.port.out.LoadInviteCodePort;
import makeus.cmc.malmo.application.port.out.LoadMemberPort;
import makeus.cmc.malmo.application.port.out.ValidateInviteCodePort;
import makeus.cmc.malmo.domain.exception.InviteCodeGenerateFailedException;
import makeus.cmc.malmo.domain.exception.InviteCodeNotFoundException;
import makeus.cmc.malmo.domain.exception.UsedInviteCodeException;
import makeus.cmc.malmo.domain.model.member.Member;
import makeus.cmc.malmo.domain.value.id.InviteCodeValue;
import makeus.cmc.malmo.domain.value.id.MemberId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class InviteCodeDomainService {

    private final LoadMemberPort loadMemberPort;
    private final LoadInviteCodePort loadInviteCodePort;
    private final ValidateInviteCodePort validateInviteCodePort;

    private final GenerateInviteCodePort generateInviteCodePort;

    private static final int MAX_RETRY = 10;

    public Member getMemberByInviteCode(InviteCodeValue inviteCode) {
        return loadMemberPort.loadMemberByInviteCode(inviteCode)
                .orElseThrow(InviteCodeNotFoundException::new);
    }

    public InviteCodeValue getInviteCodeByMemberId(MemberId memberId) {
        return loadInviteCodePort.loadInviteCodeByMemberId(memberId)
                .orElseThrow(InviteCodeNotFoundException::new);
    }

    public InviteCodeValue generateUniqueInviteCode() {
        int retryCount = 0;
        while (retryCount < MAX_RETRY) {
            String inviteCode = generateInviteCodePort.generateInviteCode();
            if (!validateInviteCodePort.validateDuplicateInviteCode(InviteCodeValue.of(inviteCode))) {
                return InviteCodeValue.of(inviteCode);
            }
            retryCount++;
        }

        throw new InviteCodeGenerateFailedException("초대 코드 생성에 실패했습니다. 재시도 횟수를 초과했습니다.");
    }

    public void validateUsedInviteCode(InviteCodeValue inviteCodeValue) {
        boolean coupleMember = validateInviteCodePort.isAlreadyCoupleMemberByInviteCode(inviteCodeValue);
        if (coupleMember) {
            throw new UsedInviteCodeException("이미 사용된 커플 코드입니다. 다른 코드를 입력해주세요.");
        }
    }

}
