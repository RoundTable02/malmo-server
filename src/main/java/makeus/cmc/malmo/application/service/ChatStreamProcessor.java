package makeus.cmc.malmo.application.service;

import lombok.RequiredArgsConstructor;
import makeus.cmc.malmo.adaptor.out.OpenAiStreamClient;
import makeus.cmc.malmo.application.port.out.RequestStreamChatPort;
import makeus.cmc.malmo.application.port.out.SendSseEventPort;
import makeus.cmc.malmo.domain.model.value.ChatRoomId;
import makeus.cmc.malmo.domain.model.value.MemberId;
import makeus.cmc.malmo.domain.service.ChatMessagesDomainService;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Component
public class ChatStreamProcessor {

    private final RequestStreamChatPort requestStreamChatPort;
    private final ChatMessagesDomainService chatMessagesDomainService;
    private final SendSseEventPort sendSseEventPort;

    private final TaskExecutor executor = new SimpleAsyncTaskExecutor();

    public void requestApiStreamAsync(MemberId memberId,
                                      List<Map<String, String>> messages,
                                      ChatRoomId chatRoomId) {
        executor.execute(() -> {
            // OpenAI API 스트리밍 호출
            requestStreamChatPort.streamChat(messages,
                    //  데이터 stream 수신 시 SSE 이벤트 전송
                    chunk -> sendSseEventPort.sendToMember(
                            memberId,
                            new SendSseEventPort.NotificationEvent(
                                    SendSseEventPort.SseEventType.CHAT_RESPONSE,
                                    chunk
                            )),
                    // 응답 완료 시 전체 응답 저장
                    fullAnswer -> chatMessagesDomainService.createAiTextMessage(
                            chatRoomId,
                            fullAnswer
                    ));
                }
        );

    }
}
