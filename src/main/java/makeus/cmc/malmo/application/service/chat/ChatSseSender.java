package makeus.cmc.malmo.application.service.chat;

import lombok.RequiredArgsConstructor;
import makeus.cmc.malmo.application.port.out.SendSseEventPort;
import makeus.cmc.malmo.domain.value.id.MemberId;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChatSseSender {

    private final SendSseEventPort sendSseEventPort;

    public void sendResponseChunk(MemberId memberId, String chunk) {
        if (chunk.startsWith(" ")) {
            chunk = " " + chunk; // 공백 한 칸을 앞에 추가 (html event stream에서 공백이 제거되는 문제 해결)
        }
        sendSseEventPort.sendToMember(
                memberId,
                new SendSseEventPort.NotificationEvent(
                        SendSseEventPort.SseEventType.CHAT_RESPONSE,
                        chunk
                ));
    }

    public void sendAiResponseId(MemberId memberId, Long messageId) {
        sendSseEventPort.sendToMember(
                memberId,
                new SendSseEventPort.NotificationEvent(
                        SendSseEventPort.SseEventType.AI_RESPONSE_ID,
                        messageId
                ));
    }

    public void sendError(MemberId memberId, String errorMessage) {
        sendSseEventPort.sendToMember(
                memberId,
                new SendSseEventPort.NotificationEvent(
                        SendSseEventPort.SseEventType.CHAT_RESPONSE,
                        errorMessage
                ));
    }

    public void sendFlowEvent(MemberId memberId, SendSseEventPort.SseEventType eventType, String message) {
        sendSseEventPort.sendToMember(
                memberId,
                new SendSseEventPort.NotificationEvent(eventType, message)
        );
    }

    public void sendLastResponse(MemberId memberId, String message) {
        sendSseEventPort.sendToMember(
                memberId,
                new SendSseEventPort.NotificationEvent(
                        SendSseEventPort.SseEventType.CHAT_RESPONSE,
                        message
                ));
    }
}
