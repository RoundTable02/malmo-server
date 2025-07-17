package makeus.cmc.malmo.domain.service;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import makeus.cmc.malmo.application.port.out.LoadLoveTypeDataPort;
import makeus.cmc.malmo.domain.model.love_type.LoveTypeCategory;
import makeus.cmc.malmo.domain.model.love_type.LoveTypeData;
import org.springframework.stereotype.Component;

import java.util.Map;

@RequiredArgsConstructor
@Component
public class LoveTypeDataService {

    private final LoadLoveTypeDataPort loadLoveTypeDataPort;
    private Map<LoveTypeCategory, LoveTypeData> loveTypeDataMap;

    @PostConstruct
    public void init() {
        loveTypeDataMap = loadLoveTypeDataPort.loadLoveTypeData();
    }

    public LoveTypeData getLoveTypeData(LoveTypeCategory category) {
        return loveTypeDataMap.get(category);
    }
}
