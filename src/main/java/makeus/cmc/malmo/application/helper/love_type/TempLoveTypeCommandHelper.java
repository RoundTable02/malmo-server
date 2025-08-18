package makeus.cmc.malmo.application.helper.love_type;

import lombok.RequiredArgsConstructor;
import makeus.cmc.malmo.application.port.out.SaveTempLoveTypePort;
import makeus.cmc.malmo.domain.model.love_type.TempLoveType;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class TempLoveTypeCommandHelper {

    private final SaveTempLoveTypePort saveTempLoveTypePort;

    public TempLoveType saveTempLoveType(TempLoveType tempLoveType) {
        return saveTempLoveTypePort.saveTempLoveType(tempLoveType);
    }
}
