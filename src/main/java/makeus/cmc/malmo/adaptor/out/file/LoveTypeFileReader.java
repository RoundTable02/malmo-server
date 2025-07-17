package makeus.cmc.malmo.adaptor.out.file;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import makeus.cmc.malmo.adaptor.out.exception.JsonParsingException;
import makeus.cmc.malmo.application.port.out.LoadLoveTypeDataPort;
import makeus.cmc.malmo.domain.model.love_type.LoveTypeCategory;
import makeus.cmc.malmo.domain.model.love_type.LoveTypeData;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class LoveTypeFileReader implements LoadLoveTypeDataPort {

    private final ObjectMapper objectMapper;

    @Override
    public Map<LoveTypeCategory, LoveTypeData> loadLoveTypeData() {
        InputStream is = getClass().getClassLoader().getResourceAsStream("love-type.json");
        List<LoveTypeData> loveTypeDataList;
        try {
            loveTypeDataList = objectMapper.readValue(is, new TypeReference<>() {});
        } catch (IOException e) {
            throw new JsonParsingException("Failed to parse love type data from JSON file", e);
        }

        Map<LoveTypeCategory, LoveTypeData> map = new HashMap<>();
        for (LoveTypeData data : loveTypeDataList) {
            map.put(data.getCategory(), data);
        }
        return map;
    }
}
