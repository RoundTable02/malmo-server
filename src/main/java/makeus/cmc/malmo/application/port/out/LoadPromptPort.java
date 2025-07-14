package makeus.cmc.malmo.application.port.out;

import makeus.cmc.malmo.domain.model.chat.Prompt;

import java.util.Optional;

public interface LoadPromptPort {
    Optional<Prompt> loadPromptMinLevelPrompt();
}
