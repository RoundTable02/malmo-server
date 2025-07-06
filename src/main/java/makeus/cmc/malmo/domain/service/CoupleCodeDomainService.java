package makeus.cmc.malmo.domain.service;

import lombok.RequiredArgsConstructor;
import makeus.cmc.malmo.adaptor.out.persistence.exception.CoupleCodeNotFoundException;
import makeus.cmc.malmo.adaptor.out.persistence.exception.MemberNotFoundException;
import makeus.cmc.malmo.application.port.out.LoadCoupleCodePort;
import makeus.cmc.malmo.application.port.out.LoadMemberPort;
import makeus.cmc.malmo.domain.model.member.CoupleCode;
import makeus.cmc.malmo.domain.model.member.Member;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CoupleCodeDomainService {

    private final LoadCoupleCodePort loadCoupleCodePort;

    public CoupleCode getCoupleCodeByInviteCode(String inviteCode) {
        return loadCoupleCodePort.loadCoupleCodeByInviteCode(inviteCode)
                .orElseThrow(CoupleCodeNotFoundException::new);
    }

}
