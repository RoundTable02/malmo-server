package makeus.cmc.malmo.application.port.out;

import makeus.cmc.malmo.domain.model.love_type.TempLoveType;

import java.util.Optional;

public interface LoadTempLoveTypePort {
    Optional<TempLoveType> loadTempLoveTypeById(Long tempLoveTypeId);
}
