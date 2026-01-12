package makeus.cmc.malmo.application.helper.chat_room;

import lombok.RequiredArgsConstructor;
import makeus.cmc.malmo.adaptor.out.persistence.repository.chat.DetailedPromptRepository;
import makeus.cmc.malmo.adaptor.out.persistence.mapper.DetailedPromptMapper;
import makeus.cmc.malmo.domain.model.chat.DetailedPrompt;
import org.springframework.stereotype.Component;

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

    /**
     * DetailedPrompt 조회 (fallback 지원)
     * 요청한 레벨의 프롬프트가 없으면 3단계 1번 프롬프트 반환
     */
    public DetailedPrompt getGuidelinePromptWithFallback(int level, int detailedLevel) {
        Optional<DetailedPrompt> prompt = detailedPromptRepository.findByLevelAndDetailedLevelAndIsForGuidelineTrue(level, detailedLevel)
                .map(detailedPromptMapper::toDomain);
        
        if (prompt.isEmpty()) {
            // 4단계 이상: 3단계 1번 프롬프트 재사용
            return detailedPromptRepository.findByLevelAndDetailedLevelAndIsForGuidelineTrue(3, 1)
                    .map(detailedPromptMapper::toDomain)
                    .orElseThrow(() -> new RuntimeException("Fallback prompt not found"));
        }
        
        return prompt.get();
    }
}
