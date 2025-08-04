package makeus.cmc.malmo.application.service.chat;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import makeus.cmc.malmo.application.port.out.chat.RequestChatApiPort;
import makeus.cmc.malmo.domain.model.chat.Prompt;
import makeus.cmc.malmo.domain.value.type.SenderType;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatProcessor {

    private final RequestChatApiPort requestChatApiPort;
    private final ObjectMapper objectMapper;

    public void streamChat(List<Map<String, String>> messages,
                           Prompt systemPrompt,
                           Prompt prompt,
                           Consumer<String> onChunk,
                           Consumer<String> onComplete,
                           Consumer<String> onError) {

        messages.add(createMessageMap(SenderType.SYSTEM, systemPrompt.getContent()));
        messages.add(createMessageMap(SenderType.SYSTEM, prompt.getContent()));

        log.info("total messages: {}", messages);

        requestChatApiPort.streamChat(messages, onChunk, onComplete, onError);
    }

    public void requestSummaryAsync(List<Map<String, String>> messages,
                                    Prompt systemPrompt,
                                    Prompt prompt,
                                    Prompt summaryPrompt,
                                    Consumer<String> onSummary) {

        messages.add(createMessageMap(SenderType.SYSTEM, systemPrompt.getContent()));
        messages.add(createMessageMap(SenderType.SYSTEM, prompt.getContent()));
        messages.add(createMessageMap(SenderType.SYSTEM, "[현재 단계 지시] " + summaryPrompt.getContent()));

        requestChatApiPort.requestSummary(messages, onSummary);
    }

    public CounselingSummary requestTotalSummary(List<Map<String, String>> messages,
                                                 Prompt systemPrompt,
                                                 Prompt totalSummaryPrompt) {
        messages.add(createMessageMap(SenderType.SYSTEM, systemPrompt.getContent()));
        messages.add(createMessageMap(SenderType.SYSTEM, "[현재 단계 지시] " + totalSummaryPrompt.getContent()));

        String summaryJson = requestChatApiPort.requestTotalSummary(messages);

        try {
            return objectMapper.readValue(summaryJson, CounselingSummary.class);
        } catch (JsonProcessingException e) {
            // TODO : 적절한 예외 처리 로직 추가
            log.error("Failed to parse summary JSON: {}", summaryJson, e);
            throw new RuntimeException("Failed to parse summary JSON", e);
        }
    }

    private Map<String, String> createMessageMap(SenderType senderType, String content) {
        return Map.of(
                "role", senderType.getApiName(),
                "content", content
        );
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CounselingSummary {
        private String totalSummary;
        private String situationKeyword;
        private String solutionKeyword;
    }
}
