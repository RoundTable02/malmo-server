package makeus.cmc.malmo.application.service.helper.love_type;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import makeus.cmc.malmo.application.port.out.LoadLoveTypeQuestionDataPort;
import makeus.cmc.malmo.domain.exception.LoveTypeQuestionNotFoundException;
import makeus.cmc.malmo.domain.model.love_type.LoveTypeQuestionData;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class LoveTypeQueryHelper {

    private final LoadLoveTypeQuestionDataPort loadPort;
    private Map<Long, LoveTypeQuestionData> questionMap;

    @PostConstruct
    public void init() {
        questionMap = loadPort.loadLoveTypeData();
    }

    public LoveTypeQuestionData getQuestionById(Long id) {
        LoveTypeQuestionData question = questionMap.get(id);
        if (question == null) {
            throw new LoveTypeQuestionNotFoundException();
        }
        return question;
    }
}
