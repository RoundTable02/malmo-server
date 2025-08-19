package makeus.cmc.malmo.application.port.out;

import makeus.cmc.malmo.domain.model.love_type.TempLoveType;

public interface SaveTempLoveTypePort {
    TempLoveType saveTempLoveType(TempLoveType tempLoveType);
}
