package makeus.cmc.malmo.application.service.chat;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import makeus.cmc.malmo.application.port.out.chat.RequestChatApiPort;
import makeus.cmc.malmo.application.port.in.chat.SufficiencyCheckResult;
import makeus.cmc.malmo.domain.model.chat.DetailedPrompt;
import makeus.cmc.malmo.domain.model.chat.Prompt;
import makeus.cmc.malmo.domain.value.type.SenderType;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatProcessor {

    private final RequestChatApiPort requestChatApiPort;
    private final ObjectMapper objectMapper;

    public Mono<Void> streamChat(List<Map<String, String>> messages,
                                 Prompt systemPrompt,
                                 Prompt prompt,
                                 DetailedPrompt detailedPrompt,
                                 Consumer<String> onChunk,
                                 Consumer<String> onComplete,
                                 Consumer<String> onError) {

        messages.add(createMessageMap(SenderType.SYSTEM, systemPrompt.getContent()));
        messages.add(createMessageMap(SenderType.SYSTEM, prompt.getContent()));
        messages.add(createMessageMap(SenderType.SYSTEM, detailedPrompt.getContent()));

        log.info("Starting streamChat with messages: {}", messages);

        return requestChatApiPort.requestStreamResponse(messages, onChunk) // onChunk 콜백만 넘김
                .flatMap(fullAnswer -> {
                    // 스트림이 성공적으로 완료되고 전체 응답(fullAnswer)이 오면 onComplete 로직 실행
                    onComplete.accept(fullAnswer);
                    log.info("Stream completed with full answer: {}", fullAnswer);
                    return Mono.empty(); // 성공적으로 완료했음을 알리기 위해 비어있는 Mono 반환
                })
                .doOnError(throwable -> onError.accept(throwable.getMessage())) // 에러 발생 시 onError 콜백 실행
                .then(); // 최종적으로 Mono<Void>로 변환하여 작업의 완료를 알림
    }

    public CompletableFuture<String> requestSummaryAsync(List<Map<String, String>> messages,
                                                         Prompt systemPrompt,
                                                         Prompt prompt,
                                                         Prompt summaryPrompt) {

        messages.add(createMessageMap(SenderType.SYSTEM, systemPrompt.getContent()));
        messages.add(createMessageMap(SenderType.SYSTEM, prompt.getContent()));
        messages.add(createMessageMap(SenderType.SYSTEM, "[현재 단계 지시] " + summaryPrompt.getContent()));

        return requestChatApiPort.requestResponse(messages);
    }

    public CompletableFuture<CounselingSummary> requestTotalSummary(List<Map<String, String>> messages,
                                                                    Prompt systemPrompt,
                                                                    Prompt totalSummaryPrompt) {
        messages.add(createMessageMap(SenderType.SYSTEM, systemPrompt.getContent()));
        messages.add(createMessageMap(SenderType.SYSTEM, "[현재 단계 지시] " + totalSummaryPrompt.getContent()));

        // 비동기 API 호출 후 CompletableFuture<String> 형태로 응답
        return requestChatApiPort.requestJsonResponse(messages)
                // 응답(JSON 문자열)이 오면, thenApply를 통해 다음 작업을 연결
                .thenApply(summaryJson -> {
                    try {
                        // JSON 문자열을 CounselingSummary 객체로 파싱
                        return objectMapper.readValue(summaryJson, CounselingSummary.class);
                    } catch (JsonProcessingException e) {
                        log.error("Failed to parse summary JSON: {}", summaryJson, e);
                        // 예외 발생 시, 런타임 예외로 감싸서 CompletableFuture가 예외를 인지
                        throw new RuntimeException("Failed to parse summary JSON", e);
                    }
                });
    }

    public CompletableFuture<String> requestMetaData(String question,
                                  String memberAnswer,
                                  Prompt metadataPrompt) {
        List<Map<String, String>> messages = List.of(
                createMessageMap(SenderType.SYSTEM, metadataPrompt.getContent()),
                createMessageMap(SenderType.ASSISTANT, "[질문] " + question),
                createMessageMap(SenderType.USER, "[답변] " + memberAnswer)
        );

        return requestChatApiPort.requestResponse(messages);
    }

    public CompletableFuture<SufficiencyCheckResult> requestSufficiencyCheck(List<Map<String, String>> messages,
                                                                             DetailedPrompt validationPrompt) {
        messages.add(createMessageMap(SenderType.SYSTEM, validationPrompt.getContent()));

        log.info("Requesting sufficiency check with messages: {}", messages);

        return requestChatApiPort.requestJsonResponse(messages)
                .thenApply(jsonResponse -> {
                    try {
                        log.info("Received sufficiency check JSON: {}", jsonResponse);
                        return objectMapper.readValue(jsonResponse, SufficiencyCheckResult.class);
                    } catch (JsonProcessingException e) {
                        log.error("Failed to parse sufficiency check JSON: {}", jsonResponse, e);
                        throw new RuntimeException("Failed to parse sufficiency check JSON", e);
                    }
                });
    }

    public CompletableFuture<String> requestDetailedSummary(List<Map<String, String>> messages,
                                                           DetailedPrompt summaryPrompt) {
        messages.add(createMessageMap(SenderType.SYSTEM, summaryPrompt.getContent()));
        return requestChatApiPort.requestResponse(messages);
    }

    public CompletableFuture<String> requestStageSummary(List<Map<String, String>> messages,
                                                        Prompt systemPrompt,
                                                        Prompt prompt,
                                                        Prompt summaryPrompt) {
        messages.add(createMessageMap(SenderType.SYSTEM, systemPrompt.getContent()));
        messages.add(createMessageMap(SenderType.SYSTEM, prompt.getContent()));
        messages.add(createMessageMap(SenderType.SYSTEM, summaryPrompt.getContent()));
        return requestChatApiPort.requestResponse(messages);
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
        private String counselingType;
    }
}
