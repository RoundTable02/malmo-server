package makeus.cmc.malmo.application.service;

import lombok.RequiredArgsConstructor;
import makeus.cmc.malmo.adaptor.out.persistence.exception.CoupleCodeNotFoundException;
import makeus.cmc.malmo.adaptor.out.persistence.exception.MemberNotFoundException;
import makeus.cmc.malmo.application.port.in.CoupleLinkUseCase;
import makeus.cmc.malmo.application.port.out.LoadCoupleCodePort;
import makeus.cmc.malmo.application.port.out.LoadMemberPort;
import makeus.cmc.malmo.application.port.out.SaveCouplePort;
import makeus.cmc.malmo.domain.model.couple.Couple;
import makeus.cmc.malmo.domain.model.couple.CoupleState;
import makeus.cmc.malmo.domain.model.member.CoupleCode;
import makeus.cmc.malmo.domain.model.member.Member;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CoupleService implements CoupleLinkUseCase {

    private final LoadMemberPort loadMemberPort;
    private final LoadCoupleCodePort loadCoupleCodePort;

    private final SaveCouplePort saveCouplePort;

    @Override
    @Transactional
    public CoupleLinkResponse coupleLink(CoupleLinkCommand command) {
        Member member = loadMemberPort.loadMemberById(command.getUserId())
                .orElseThrow(MemberNotFoundException::new);
        CoupleCode coupleCode = loadCoupleCodePort.loadCoupleCodeByInviteCode(command.getCoupleCode())
                .orElseThrow(CoupleCodeNotFoundException::new);

        Couple couple = Couple.createCouple(
                member.getId(),
                coupleCode.getMemberId().getValue(),
                coupleCode.getStartLoveDate(),
                CoupleState.ALIVE
        );

        Couple savedCouple = saveCouplePort.saveCouple(couple);

        return CoupleLinkResponse.builder()
                .coupleId(savedCouple.getId())
                .build();
    }
}
