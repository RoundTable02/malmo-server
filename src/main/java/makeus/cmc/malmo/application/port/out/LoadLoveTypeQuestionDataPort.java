package makeus.cmc.malmo.application.port.out;

import makeus.cmc.malmo.domain.model.love_type.LoveTypeCategory;
import makeus.cmc.malmo.domain.model.love_type.LoveTypeData;
import makeus.cmc.malmo.domain.model.love_type.LoveTypeQuestionData;

import java.util.Map;

public interface LoadLoveTypeQuestionDataPort {
    Map<Long, LoveTypeQuestionData> loadLoveTypeData();
}
