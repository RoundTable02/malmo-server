package makeus.cmc.malmo.adaptor.out.persistence.adapter;

import lombok.RequiredArgsConstructor;
import makeus.cmc.malmo.adaptor.out.persistence.repository.member.MemberRepository;
import makeus.cmc.malmo.application.port.out.member.ValidateInviteCodePort;
import makeus.cmc.malmo.application.port.out.member.ValidateMemberPort;
import makeus.cmc.malmo.domain.value.id.InviteCodeValue;
import makeus.cmc.malmo.domain.value.id.MemberId;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ValidateMemberAdapter implements ValidateMemberPort, ValidateInviteCodePort {

    private final MemberRepository memberRepository;

    @Override
    public boolean isCoupleMember(MemberId memberId) {
        return memberRepository.isCoupleMember(memberId.getValue());
    }

    @Override
    public boolean isValidMember(MemberId memberId) {
        return memberRepository.isMemberStateAlive(memberId.getValue());
    }

    @Override
    public boolean isAlreadyCoupleMemberByInviteCode(InviteCodeValue inviteCode) {
        return memberRepository.isAlreadyCoupleMemberByInviteCode(inviteCode.getValue());
    }

    @Override
    public boolean isCodeOwnerMemberAlreadyCoupledWith(InviteCodeValue inviteCodeValue, MemberId memberId) {
        return memberRepository.isCodeOwnerMemberAlreadyCoupledWith(inviteCodeValue.getValue(), memberId.getValue());
    }


    @Override
    public boolean isInviteCodeDuplicated(InviteCodeValue inviteCodeValue) {
        return memberRepository.existsByInviteCode(inviteCodeValue.getValue());
    }
}
