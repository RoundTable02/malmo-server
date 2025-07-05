package makeus.cmc.malmo.application.port.out;

import makeus.cmc.malmo.domain.model.love_type.LoveType;
import makeus.cmc.malmo.domain.model.love_type.LoveTypeCategory;

import java.util.Optional;

public interface LoadLoveTypePort {
    Optional<LoveType> findLoveTypeById(Long loveTypeId);
    Optional<LoveType> findLoveTypeByLoveTypeCategory(LoveTypeCategory category);
}
