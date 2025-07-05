package makeus.cmc.malmo.adaptor.out.persistence;

import lombok.RequiredArgsConstructor;
import makeus.cmc.malmo.adaptor.out.persistence.entity.love_type.LoveTypeQuestionEntity;
import makeus.cmc.malmo.adaptor.out.persistence.mapper.LoveTypeQuestionMapper;
import makeus.cmc.malmo.adaptor.out.persistence.repository.LoveTypeQuestionRepository;
import makeus.cmc.malmo.application.port.out.LoadLoveTypeQuestionsPort;
import makeus.cmc.malmo.domain.model.love_type.LoveTypeQuestion;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class LoveTypeQuestionAdapter implements LoadLoveTypeQuestionsPort {

    private final LoveTypeQuestionRepository loveTypeQuestionRepository;
    private final LoveTypeQuestionMapper loveTypeQuestionMapper;

    @Override
    public List<LoveTypeQuestion> loadLoveTypeQuestions() {
        List<LoveTypeQuestionEntity> entityList = loveTypeQuestionRepository.findAllByOrderByQuestionNumberAsc();

        return entityList.stream()
                .map(loveTypeQuestionMapper::toDomain)
                .toList();
    }
}
