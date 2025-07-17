package makeus.cmc.malmo.application.port.out;

import makeus.cmc.malmo.domain.model.love_type.LoveType;
import makeus.cmc.malmo.domain.model.love_type.LoveTypeCategory;
import makeus.cmc.malmo.domain.model.love_type.LoveTypeData;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface LoadLoveTypeDataPort {
    Map<LoveTypeCategory, LoveTypeData> loadLoveTypeData();
}
