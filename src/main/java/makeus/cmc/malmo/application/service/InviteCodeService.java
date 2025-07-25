package makeus.cmc.malmo.application.service;

import lombok.RequiredArgsConstructor;
import makeus.cmc.malmo.adaptor.in.aop.CheckValidMember;
import makeus.cmc.malmo.application.port.in.GetInviteCodeUseCase;
import makeus.cmc.malmo.domain.service.InviteCodeDomainService;
import makeus.cmc.malmo.domain.value.id.InviteCodeValue;
import makeus.cmc.malmo.domain.value.id.MemberId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class InviteCodeService implements GetInviteCodeUseCase {

    private final InviteCodeDomainService inviteCodeDomainService;

    @Override
    @CheckValidMember
    public InviteCodeResponseDto getInviteCode(InviteCodeCommand command) {
        InviteCodeValue inviteCode = inviteCodeDomainService.getInviteCodeByMemberId(MemberId.of(command.getUserId()));
        return InviteCodeResponseDto.builder()
                .coupleCode(inviteCode.getValue())
                .build();
    }
}
