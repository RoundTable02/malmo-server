package makeus.cmc.malmo.adaptor.out;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import makeus.cmc.malmo.adaptor.out.exception.AiClientRequestException;
import makeus.cmc.malmo.application.port.out.RequestStreamChatPort;
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
public class OpenAiStreamClient implements RequestStreamChatPort {

    @Value("${openai.api.key}")
    private String openAiApiKey;

    private static final String OPENAI_CHAT_URL = "https://api.openai.com/v1/chat/completions";

    private final OkHttpClient client = new OkHttpClient();

    @Override
    public void streamChat(List<Map<String, String>> messages,
                           Consumer<String> onData,
                           Consumer<String> onCompleteFullResponse) {

        Map<String, Object> body = createBody(messages);
        Request request = createRequest(body);

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                // TODO : 에러 콜백 처리
                throw new AiClientRequestException("Failed to connect to OpenAI API", e);
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) {
                StringBuilder fullResponse = new StringBuilder();
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(response.body().byteStream()))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        if (line.startsWith("data: ")) {
                            String data = line.substring(6).trim();
                            if (data.equals("[DONE]")) break;

                            String content = extractContent(data);

                            if (!content.isEmpty()) {
                                fullResponse.append(content);
                                onData.accept(content);
                            }
                        }
                    }

                    onCompleteFullResponse.accept(fullResponse.toString());
                }
                catch (Exception e) {
                    // TODO : 에러 콜백 처리
                    throw new AiClientRequestException("Error processing OpenAI API response", e);
                }
            }
        });
    }

    private String extractContent(String data) throws JsonProcessingException {
        JsonNode node = new ObjectMapper().readTree(data);
        return node
                .path("choices").get(0)
                .path("delta")
                .path("content")
                .asText();
    }

    private Request createRequest(Map<String, Object> body) {
        Request request;
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
            throw new AiClientRequestException("Failed to create request body for OpenAI API", e);
        }
        return request;
    }

    private Map<String, Object> createBody(List<Map<String, String>> messages) {
        return Map.of(
                "model", "gpt-3.5-turbo",
                "messages", messages,
                "temperature", 0.7,
                "stream", true
        );
    }
}

