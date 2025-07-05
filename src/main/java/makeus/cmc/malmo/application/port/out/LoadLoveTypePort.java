package makeus.cmc.malmo.application.port.out;

import makeus.cmc.malmo.domain.model.love_type.LoveType;

import java.util.Optional;

public interface LoadLoveTypePort {
    Optional<LoveType> findLoveTypeById(Long loveTypeId);
}
