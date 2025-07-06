package makeus.cmc.malmo.domain.service;

import lombok.RequiredArgsConstructor;
import makeus.cmc.malmo.domain.exception.CoupleCodeNotFoundException;
import makeus.cmc.malmo.domain.exception.InviteCodeGenerateFailedException;
import makeus.cmc.malmo.application.port.out.GenerateInviteCodePort;
import makeus.cmc.malmo.application.port.out.LoadCoupleCodePort;
import makeus.cmc.malmo.application.port.out.SaveCoupleCodePort;
import makeus.cmc.malmo.domain.exception.UsedCoupleCodeException;
import makeus.cmc.malmo.domain.model.member.CoupleCode;
import makeus.cmc.malmo.domain.model.member.Member;
import makeus.cmc.malmo.domain.model.value.MemberId;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CoupleCodeDomainService {

    private final LoadCoupleCodePort loadCoupleCodePort;

    private final GenerateInviteCodePort generateInviteCodePort;
    private final SaveCoupleCodePort saveCoupleCodePort;
    private static final int MAX_RETRY = 10;

    public CoupleCode getCoupleCodeByInviteCode(String inviteCode) {
        CoupleCode coupleCode = loadCoupleCodePort.loadCoupleCodeByInviteCode(inviteCode)
                .orElseThrow(CoupleCodeNotFoundException::new);

        if (coupleCode.isUsed()) {
            throw new UsedCoupleCodeException("이미 사용된 커플 코드입니다.");
        }

        return coupleCode;
    }

    public CoupleCode getCoupleCodeByMemberId(MemberId memberId) {
        return loadCoupleCodePort.loadCoupleCodeByMemberId(memberId)
                .orElseThrow(CoupleCodeNotFoundException::new);
    }

    @Transactional
    public CoupleCode generateAndSaveUniqueCoupleCode(Member member, LocalDate loveStartDate) {
        int retryCount = 0;
        while (retryCount < MAX_RETRY) {
            String inviteCode = generateInviteCodePort.generateInviteCode();
            try {
                CoupleCode coupleCode = member.generateCoupleCode(inviteCode, loveStartDate);
                saveCoupleCodePort.saveCoupleCode(coupleCode);
                return coupleCode;
            } catch (DataIntegrityViolationException e) {
                retryCount++;
            }
        }

        throw new InviteCodeGenerateFailedException("커플 코드 생성에 실패했습니다. 재시도 횟수를 초과했습니다.");
    }

}
