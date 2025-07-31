package makeus.cmc.malmo.application.service.helper.couple;

import lombok.RequiredArgsConstructor;
import makeus.cmc.malmo.application.port.out.SaveCouplePort;
import makeus.cmc.malmo.domain.model.couple.Couple;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CoupleCommandHelper {
    private final SaveCouplePort saveCouplePort;

    public void saveCouple(Couple couple) {
        saveCouplePort.saveCouple(couple);
    }
}
