package makeus.cmc.malmo.domain.service;

import lombok.RequiredArgsConstructor;
import makeus.cmc.malmo.application.port.out.LoadPromptPort;
import makeus.cmc.malmo.domain.exception.PromptNotFoundException;
import makeus.cmc.malmo.domain.model.chat.Prompt;
import org.springframework.stereotype.Service;

import static makeus.cmc.malmo.domain.model.chat.ChatRoomConstant.*;

@RequiredArgsConstructor
@Service
public class PromptDomainService {
    private final LoadPromptPort loadPromptPort;

    public Prompt getPromptByLevel(int level) {
        return loadPromptPort.loadPromptByLevel(level)
                .orElseThrow(PromptNotFoundException::new);
    }

    public Prompt getSystemPrompt() {
        return loadPromptPort.loadPromptByLevel(SYSTEM_PROMPT_LEVEL)
                .orElseThrow(PromptNotFoundException::new);
    }

    public Prompt getSummaryPrompt() {
        return loadPromptPort.loadPromptByLevel(SUMMARY_PROMPT_LEVEL)
                .orElseThrow(PromptNotFoundException::new);
    }

    public Prompt getTotalSummaryPrompt() {
        return loadPromptPort.loadPromptByLevel(TOTAL_SUMMARY_PROMPT_LEVEL)
                .orElseThrow(PromptNotFoundException::new);
    }
}
