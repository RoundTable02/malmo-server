package makeus.cmc.malmo.application.helper.chat_room;

import lombok.RequiredArgsConstructor;
import makeus.cmc.malmo.application.exception.PromptNotFoundException;
import makeus.cmc.malmo.application.port.out.chat.LoadPromptPort;
import makeus.cmc.malmo.domain.model.chat.Prompt;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class PromptQueryHelper {
    private final LoadPromptPort loadPromptPort;

    public Prompt getSystemPrompt() {
        return loadPromptPort.loadSystemPrompt()
                .orElseThrow(PromptNotFoundException::new);
    }

    @Deprecated
    public Prompt getSummaryPrompt() {
        return loadPromptPort.loadSummaryPrompt()
                .orElseThrow(PromptNotFoundException::new);
    }

    public Prompt getSummaryPrompt(int level) {
        return loadPromptPort.loadSummaryPromptByLevel(level)
                .orElseThrow(PromptNotFoundException::new);
    }

    public Prompt getTotalSummaryPrompt() {
        return loadPromptPort.loadTotalSummaryPrompt()
                .orElseThrow(PromptNotFoundException::new);
    }

    public Prompt getGuidelinePrompt(int level) {
        return loadPromptPort.loadGuidelinePrompt(level)
                .orElseThrow(PromptNotFoundException::new);
    }

    public Prompt getAnswerMetadataPrompt() {
        return loadPromptPort.loadAnswerMetadataPrompt()
                .orElseThrow(PromptNotFoundException::new);
    }
}
