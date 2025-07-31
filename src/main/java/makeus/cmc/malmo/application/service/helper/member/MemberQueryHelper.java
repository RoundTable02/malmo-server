package makeus.cmc.malmo.application.service.helper.member;

import lombok.RequiredArgsConstructor;
import makeus.cmc.malmo.application.port.out.LoadInviteCodePort;
import makeus.cmc.malmo.application.port.out.LoadMemberPort;
import makeus.cmc.malmo.application.port.out.ValidateInviteCodePort;
import makeus.cmc.malmo.application.port.out.ValidateMemberPort;
import makeus.cmc.malmo.domain.exception.*;
import makeus.cmc.malmo.domain.model.member.Member;
import makeus.cmc.malmo.domain.value.id.InviteCodeValue;
import makeus.cmc.malmo.domain.value.id.MemberId;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MemberQueryHelper {

    private final LoadMemberPort loadMemberPort;
    private final LoadInviteCodePort loadInviteCodePort;
    private final ValidateInviteCodePort validateInviteCodePort;
    private final ValidateMemberPort validateMemberPort;

    public Member getMemberByIdOrThrow(MemberId memberId) {
        return loadMemberPort.loadMemberById(MemberId.of(memberId.getValue()))
                .orElseThrow(MemberNotFoundException::new);
    }

    public Member getMemberByInviteCodeOrThrow(InviteCodeValue inviteCode) {
        return loadMemberPort.loadMemberByInviteCode(inviteCode).orElseThrow(InviteCodeNotFoundException::new);
    }

    public InviteCodeValue getInviteCodeByMemberIdOrThrow(MemberId memberId) {
        return loadInviteCodePort.loadInviteCodeByMemberId(memberId)
                .orElseThrow(InviteCodeNotFoundException::new);
    }

    public boolean isInviteCodeValid(InviteCodeValue inviteCode) {
        return !validateInviteCodePort.isInviteCodeDuplicated(inviteCode);
    }

    public void validateUsedInviteCode(InviteCodeValue inviteCodeValue) {
        boolean coupleMember = validateInviteCodePort.isAlreadyCoupleMemberByInviteCode(inviteCodeValue);
        if (coupleMember) {
            throw new UsedInviteCodeException("이미 사용된 커플 코드입니다. 다른 코드를 입력해주세요.");
        }
    }

    public void validateMemberNotCoupled(MemberId memberId) {
        boolean coupleMember = validateMemberPort.isCoupleMember(memberId);

        if (coupleMember) {
            throw new AlreadyCoupledMemberException("이미 커플로 등록된 사용자입니다. 커플 등록을 해제 후 이용해주세요.");
        }
    }

    public void validateOwnInviteCode(MemberId memberId, InviteCodeValue inviteCode) {
        Member member = loadMemberPort.loadMemberById(memberId)
                .orElseThrow(MemberNotFoundException::new);

        if (member.getInviteCode().equals(inviteCode)) {
            throw new NotValidCoupleCodeException("본인의 초대코드를 사용할 수 없습니다.");
        }
    }

}
