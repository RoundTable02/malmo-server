package makeus.cmc.malmo.application.port.out.chat;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public interface RequestChatApiPort {
    void requestStreamResponse(
            List<Map<String, String>> messages,
            Consumer<String> onData,
            Consumer<String> onCompleteFullResponse,
            Consumer<String> onError);

    String requestResponse(List<Map<String, String>> messages);

    String requestJsonResponse(List<Map<String, String>> messages);
}
