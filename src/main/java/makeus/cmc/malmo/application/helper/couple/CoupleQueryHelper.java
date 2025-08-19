package makeus.cmc.malmo.application.helper.couple;

import lombok.RequiredArgsConstructor;
import makeus.cmc.malmo.application.exception.AlreadyCoupledMemberException;
import makeus.cmc.malmo.application.exception.NotCoupleMemberException;
import makeus.cmc.malmo.application.port.out.couple.LoadCouplePort;
import makeus.cmc.malmo.application.port.out.member.LoadMemberPort;
import makeus.cmc.malmo.domain.model.couple.Couple;
import makeus.cmc.malmo.domain.value.id.CoupleId;
import makeus.cmc.malmo.domain.value.id.CoupleMemberId;
import makeus.cmc.malmo.domain.value.id.MemberId;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class CoupleQueryHelper {

    private final LoadCouplePort loadCouplePort;
    private final LoadMemberPort loadMemberPort;

    public CoupleId getCoupleIdByMemberId(MemberId memberId) {
        return loadMemberPort.loadCoupleIdByMemberId(memberId);
    }

    public Couple getCoupleByMemberIdOrThrow(MemberId memberId) {
        return loadCouplePort.loadCoupleByMemberId(memberId)
                .orElseThrow(NotCoupleMemberException::new);
    }

    public Optional<Couple> getCoupleByMemberId(MemberId memberId) {
        return loadCouplePort.loadCoupleByMemberId(memberId);
    }

    public Optional<Couple> getCoupleById(CoupleId coupleId) {
        return loadCouplePort.loadCoupleByCoupleId(coupleId);
    }

    public Couple getCoupleByIdOrThrow(CoupleId coupleId) {
        return loadCouplePort.loadCoupleByCoupleId(coupleId)
                .orElseThrow(() -> new NotCoupleMemberException("해당 커플을 찾을 수 없습니다."));
    }

    public Optional<Couple> getCoupleByMemberAndPartnerId(MemberId memberId, MemberId partnerId) {
        return loadCouplePort.loadCoupleByMemberIdAndPartnerId(memberId, partnerId);
    }

    public void validateBrokenCouple(CoupleId coupleId) {
        Couple couple = getCoupleByIdOrThrow(coupleId);
        if (!couple.isBroken()) {
            throw new AlreadyCoupledMemberException("이미 커플로 등록된 사용자입니다. 커플 등록을 해제 후 이용해주세요.");
        }
    }
}
