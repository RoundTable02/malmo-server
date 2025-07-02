package makeus.cmc.malmo.application.port.out;

import makeus.cmc.malmo.domain.model.couple.Couple;

public interface SaveCouplePort {
    Couple saveCouple(Couple couple);
}
