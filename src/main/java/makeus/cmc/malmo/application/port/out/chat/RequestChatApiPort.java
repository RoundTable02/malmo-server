package makeus.cmc.malmo.application.port.out.chat;

import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public interface RequestChatApiPort {
    Mono<String> requestStreamResponse(List<Map<String, String>> messages, Consumer<String> onData);

    CompletableFuture<String> requestResponse(List<Map<String, String>> messages);

    CompletableFuture<String> requestJsonResponse(List<Map<String, String>> messages);
}
