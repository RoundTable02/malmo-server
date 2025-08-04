package makeus.cmc.malmo.application.helper.chat_room;

import lombok.RequiredArgsConstructor;
import makeus.cmc.malmo.application.port.out.chat.LoadPromptPort;
import makeus.cmc.malmo.application.exception.PromptNotFoundException;
import makeus.cmc.malmo.domain.model.chat.Prompt;
import org.springframework.stereotype.Component;

import static makeus.cmc.malmo.util.GlobalConstants.*;

@Component
@RequiredArgsConstructor
public class PromptQueryHelper {
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
