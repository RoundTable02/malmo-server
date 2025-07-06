package makeus.cmc.malmo.application.service;

import lombok.RequiredArgsConstructor;
import makeus.cmc.malmo.adaptor.out.persistence.exception.CoupleCodeNotFoundException;
import makeus.cmc.malmo.application.port.in.GetInviteCodeUseCase;
import makeus.cmc.malmo.application.port.out.LoadCoupleCodePort;
import makeus.cmc.malmo.domain.model.member.CoupleCode;
import makeus.cmc.malmo.domain.model.value.MemberId;
import makeus.cmc.malmo.domain.service.CoupleCodeDomainService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class InviteCodeService implements GetInviteCodeUseCase {

    private final CoupleCodeDomainService coupleCodeDomainService;

    @Override
    public InviteCodeResponseDto getInviteCode(InviteCodeCommand command) {
        CoupleCode coupleCode = coupleCodeDomainService.getCoupleCodeByMemberId(MemberId.of(command.getUserId()));
        return InviteCodeResponseDto.builder()
                .coupleCode(coupleCode.getInviteCode())
                .build();
    }
}
