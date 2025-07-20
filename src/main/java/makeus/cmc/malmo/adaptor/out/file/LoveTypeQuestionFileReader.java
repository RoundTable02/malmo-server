package makeus.cmc.malmo.adaptor.out.file;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import makeus.cmc.malmo.adaptor.out.exception.JsonParsingException;
import makeus.cmc.malmo.application.port.out.LoadLoveTypeQuestionDataPort;
import makeus.cmc.malmo.domain.model.love_type.LoveTypeQuestionData;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class LoveTypeQuestionFileReader implements LoadLoveTypeQuestionDataPort {

    private final ObjectMapper objectMapper;

    @Override
    public Map<Long, LoveTypeQuestionData> loadLoveTypeData() {
        InputStream is = getClass().getClassLoader().getResourceAsStream("love-type-question.json");
        List<LoveTypeQuestionData> loveTypeQuestionList;
        try {
            loveTypeQuestionList = objectMapper.readValue(is, new TypeReference<>() {});
        } catch (IOException e) {
            throw new JsonParsingException("Failed to parse love type data from JSON file", e);
        }

        Map<Long, LoveTypeQuestionData> map = new HashMap<>();
        for (LoveTypeQuestionData data : loveTypeQuestionList) {
            map.put(data.getId(), data);
        }
        return map;
    }
}
