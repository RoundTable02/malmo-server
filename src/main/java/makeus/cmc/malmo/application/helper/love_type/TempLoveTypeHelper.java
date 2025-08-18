package makeus.cmc.malmo.application.helper.love_type;

import lombok.RequiredArgsConstructor;
import makeus.cmc.malmo.application.exception.TempLoveTypeNotFoundException;
import makeus.cmc.malmo.application.port.out.LoadTempLoveTypePort;
import makeus.cmc.malmo.application.port.out.SaveTempLoveTypePort;
import makeus.cmc.malmo.domain.model.love_type.TempLoveType;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class TempLoveTypeHelper {

    private final SaveTempLoveTypePort saveTempLoveTypePort;
    private final LoadTempLoveTypePort loadTempLoveTypePort;

    public TempLoveType saveTempLoveType(TempLoveType tempLoveType) {
        return saveTempLoveTypePort.saveTempLoveType(tempLoveType);
    }

    public TempLoveType getTempLoveTypeByIdOrThrow(Long tempLoveTypeId) {
        return loadTempLoveTypePort.loadTempLoveTypeById(tempLoveTypeId)
                .orElseThrow(TempLoveTypeNotFoundException::new);
    }

}
