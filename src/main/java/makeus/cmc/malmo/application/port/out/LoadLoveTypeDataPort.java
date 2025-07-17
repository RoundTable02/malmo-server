package makeus.cmc.malmo.application.port.out;

import makeus.cmc.malmo.domain.model.love_type.LoveTypeCategory;
import makeus.cmc.malmo.domain.model.love_type.LoveTypeData;

import java.util.Map;

public interface LoadLoveTypeDataPort {
    Map<LoveTypeCategory, LoveTypeData> loadLoveTypeData();
}
