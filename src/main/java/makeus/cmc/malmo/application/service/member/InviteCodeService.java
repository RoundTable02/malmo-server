package makeus.cmc.malmo.application.service.member;

import lombok.RequiredArgsConstructor;
import makeus.cmc.malmo.adaptor.in.aop.CheckValidMember;
import makeus.cmc.malmo.application.port.in.member.GetInviteCodeUseCase;
import makeus.cmc.malmo.application.helper.member.MemberQueryHelper;
import makeus.cmc.malmo.domain.value.id.InviteCodeValue;
import makeus.cmc.malmo.domain.value.id.MemberId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class InviteCodeService implements GetInviteCodeUseCase {

    private final MemberQueryHelper memberQueryHelper;

    @Override
    @CheckValidMember
    public InviteCodeResponseDto getInviteCode(InviteCodeCommand command) {
        InviteCodeValue inviteCode = memberQueryHelper.getInviteCodeByMemberIdOrThrow(MemberId.of(command.getUserId()));
        return InviteCodeResponseDto.builder()
                .coupleCode(inviteCode.getValue())
                .build();
    }
}
