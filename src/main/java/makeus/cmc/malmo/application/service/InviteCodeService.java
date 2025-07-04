package makeus.cmc.malmo.application.service;

import lombok.RequiredArgsConstructor;
import makeus.cmc.malmo.adaptor.out.persistence.exception.CoupleCodeNotFoundException;
import makeus.cmc.malmo.application.port.in.GetInviteCodeUseCase;
import makeus.cmc.malmo.application.port.out.LoadCoupleCodePort;
import makeus.cmc.malmo.domain.model.member.CoupleCode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class InviteCodeService implements GetInviteCodeUseCase {

    private final LoadCoupleCodePort loadCoupleCodePort;

    @Override
    public InviteCodeResponseDto getInviteCode(InviteCodeCommand command) {
        CoupleCode coupleCode = loadCoupleCodePort.loadCoupleCodeByMemberId(command.getUserId())
                .orElseThrow(CoupleCodeNotFoundException::new);
        return InviteCodeResponseDto.builder()
                .coupleCode(coupleCode.getInviteCode())
                .build();
    }
}
