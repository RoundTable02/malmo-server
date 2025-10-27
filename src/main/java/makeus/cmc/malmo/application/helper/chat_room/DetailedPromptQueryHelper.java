package makeus.cmc.malmo.application.helper.chat_room;

import lombok.RequiredArgsConstructor;
import makeus.cmc.malmo.adaptor.out.persistence.repository.chat.DetailedPromptRepository;
import makeus.cmc.malmo.adaptor.out.persistence.mapper.DetailedPromptMapper;
import makeus.cmc.malmo.domain.model.chat.DetailedPrompt;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class DetailedPromptQueryHelper {

    private final DetailedPromptRepository detailedPromptRepository;
    private final DetailedPromptMapper detailedPromptMapper;

    public Optional<DetailedPrompt> getValidationPrompt(int level, int detailedLevel) {
        return detailedPromptRepository.findByLevelAndDetailedLevelAndIsForValidation(level, detailedLevel)
                .map(detailedPromptMapper::toDomain);
    }

    public Optional<DetailedPrompt> getGuidelinePrompt(int level, int detailedLevel) {
        return detailedPromptRepository.findByLevelAndDetailedLevelAndIsForGuidelineTrue(level, detailedLevel)
                .map(detailedPromptMapper::toDomain);
    }
}
