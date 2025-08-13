package makeus.cmc.malmo.adaptor.out;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import makeus.cmc.malmo.adaptor.out.exception.OpenAiRequestException;
import makeus.cmc.malmo.application.port.out.chat.RequestChatApiPort;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

@Slf4j
@Component
public class OpenAiApiClient implements RequestChatApiPort {

    public static final String GPT_VERSION = "gpt-4o";
    public static final double GPT_TEMPERATURE = 0.5;

    @Value("${openai.api.key}")
    private String openAiApiKey;

    private static final String OPENAI_CHAT_URL = "https://api.openai.com/v1/chat/completions";

    private final OkHttpClient client = new OkHttpClient();

    @Override
    public void requestStreamResponse(List<Map<String, String>> messages,
                                      Consumer<String> onData,
                                      Consumer<String> onCompleteFullResponse,
                                      Consumer<String> onError) {

        Map<String, Object> body = createStreamBody(messages);
        Request request = createStreamRequest(body, onError);

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                log.error("OpenAI API request failed with code: {}", response.code());
                onError.accept("에러가 발생했습니다: API 요청에 실패했습니다.");
                return;
            }

            StringBuilder fullResponse = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(response.body().byteStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    if (line.startsWith("data: ")) {
                        String data = line.substring(6).trim();
                        if (data.equals("[DONE]")) break;

                        String content = extractStreamContent(data);
                        if (!content.isEmpty()) {
                            fullResponse.append(content);
                            onData.accept(content); // 실시간 콜백 호출
                        }
                    }
                }
                onCompleteFullResponse.accept(fullResponse.toString());
            }
        } catch (IOException e) {
            log.error("Failed to connect to OpenAI API", e);
            onError.accept("에러가 발생했습니다: 네트워크 연결에 실패했습니다.");
        } catch (Exception e) {
            log.error("Error processing OpenAI API response", e);
            onError.accept("에러가 발생했습니다: 응답 처리 중 문제가 발생했습니다.");
        }
    }

    @Override
    public String requestResponse(List<Map<String, String>> messages) {
        Map<String, Object> body = createBody(messages);
        Request request = createRequest(body);

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                log.error("OpenAI API request failed with code: {}", response.code());
                return "";
            }

            String data = response.body().string();

            return extractContent(data);
        } catch (IOException e) {
            log.error("Failed to connect to OpenAI API", e);
        } catch (Exception e) {
            log.error("Error processing OpenAI API response", e);
        }
        return "";
    }

    @Override
    public String requestJsonResponse(List<Map<String, String>> messages) {
        // OpenAI API에 요청할 때 응답 형식을 JSON으로 지정
        Map<String, Object> body = createBodyForJsonResponse(messages);
        Request request = createRequest(body);
        log.info("Requesting OpenAI API with body: {}", body);

        try (Response response = client.newCall(request).execute()) {
            log.info("OpenAI API response code: {}", response.code());

            if (!response.isSuccessful()) {
                log.error("OpenAI API request failed with code: {}", response.code());
                log.info("Failed Body: {}", response.body().string());
                throw new OpenAiRequestException("OpenAI API request failed with code: " + response.code());
            }

            String data = response.body().string();
            log.info("OpenAI API response data: {}", extractContent(data));
            return extractContent(data);
        } catch (IOException e) {
            throw new OpenAiRequestException("Failed to connect to OpenAI API");
        } catch (Exception e) {
            e.printStackTrace();
            throw new OpenAiRequestException("Error processing OpenAI API response");
        }
    }

    private String extractStreamContent(String data) throws JsonProcessingException {
        JsonNode node = new ObjectMapper().readTree(data);
        return node
                .path("choices").get(0)
                .path("delta")
                .path("content")
                .asText();
    }

    private String extractContent(String data) throws JsonProcessingException {
        JsonNode node = new ObjectMapper().readTree(data);
        return node
                .path("choices").get(0)
                .path("message")
                .path("content")
                .asText();
    }

    private Request createStreamRequest(Map<String, Object> body, Consumer<String> onError) {
        Request request = null;
        try {
            request = new Request.Builder()
                    .url(OPENAI_CHAT_URL)
                    .post(RequestBody.create(
                            new ObjectMapper().writeValueAsString(body),
                            MediaType.get("application/json")))
                    .header("Authorization", "Bearer " + openAiApiKey)
                    .header("Content-Type", "application/json")
                    .build();
        } catch (JsonProcessingException e) {
            log.error("Error processing OpenAI API response", e);
            onError.accept("에러가 발생했습니다: 요청 생성 중 문제가 발생했습니다.");
        }
        return request;
    }

    private Request createRequest(Map<String, Object> body) {
        Request request = null;
        try {
            request = new Request.Builder()
                    .url(OPENAI_CHAT_URL)
                    .post(RequestBody.create(
                            new ObjectMapper().writeValueAsString(body),
                            MediaType.get("application/json")))
                    .header("Authorization", "Bearer " + openAiApiKey)
                    .header("Content-Type", "application/json")
                    .build();
        } catch (JsonProcessingException e) {
            log.error("Error processing OpenAI API response", e);
        }
        return request;
    }

    private Map<String, Object> createStreamBody(List<Map<String, String>> messages) {
        return Map.of(
                "model", GPT_VERSION,
                "messages", messages,
                "temperature", GPT_TEMPERATURE,
                "stream", true
        );
    }

    private Map<String, Object> createBody(List<Map<String, String>> messages) {
        return Map.of(
                "model", GPT_VERSION,
                "messages", messages,
                "temperature", GPT_TEMPERATURE,
                "stream", false
        );
    }

    private Map<String, Object> createBodyForJsonResponse(List<Map<String, String>> messages) {
        return Map.of(
                "model", GPT_VERSION,
                "response_format", Map.of("type", "json_object"),
                "messages", messages,
                "temperature", GPT_TEMPERATURE,
                "stream", false
        );
    }
}

