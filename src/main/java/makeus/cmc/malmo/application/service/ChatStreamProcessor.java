package makeus.cmc.malmo.application.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import makeus.cmc.malmo.application.port.out.RequestChatApiPort;
import makeus.cmc.malmo.application.port.out.SaveChatMessageSummaryPort;
import makeus.cmc.malmo.application.port.out.SendSseEventPort;
import makeus.cmc.malmo.domain.model.chat.ChatMessage;
import makeus.cmc.malmo.domain.model.chat.ChatMessageSummary;
import makeus.cmc.malmo.domain.model.chat.ChatRoom;
import makeus.cmc.malmo.domain.model.chat.Prompt;
import makeus.cmc.malmo.domain.service.ChatMessagesDomainService;
import makeus.cmc.malmo.domain.service.ChatRoomDomainService;
import makeus.cmc.malmo.domain.value.id.ChatRoomId;
import makeus.cmc.malmo.domain.value.id.MemberId;
import makeus.cmc.malmo.domain.value.type.SenderType;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
@RequiredArgsConstructor
@Component
public class ChatStreamProcessor {

    private final RequestChatApiPort requestChatApiPort;
    private final ChatMessagesDomainService chatMessagesDomainService;
    private final ChatRoomDomainService chatRoomDomainService;
    private final SendSseEventPort sendSseEventPort;
    private final SaveChatMessageSummaryPort saveChatMessageSummaryPort;

    public void requestApiStream(MemberId memberId,
                                 boolean isMemberCoupled,
                                 Prompt systemPrompt,
                                 Prompt prompt,
                                 List<Map<String, String>> messages,
                                 ChatRoomId chatRoomId) {
        AtomicBoolean isOkDetected = new AtomicBoolean(false);
        messages.add(
                createMessageMap(SenderType.SYSTEM, systemPrompt.getContent())
        );
        messages.add(
                createMessageMap(SenderType.SYSTEM, prompt.getContent())
        );

        // OpenAI API 스트리밍 호출
        requestChatApiPort.streamChat(messages,
                //  데이터 stream 수신 시 SSE 이벤트 전송
                chunk -> {
                    // OK 응답이 감지된 경우, 마지막 단계가 아닌 경우 => 현재 단계 종료 처리
                    if(chunk.contains("OK") && !prompt.isLastPrompt()) {
                        isOkDetected.set(true);
                    } else {
                        sendSseMessage(memberId, chunk);
                    }
                },
                // 응답 완료 시 전체 응답 저장
                fullAnswer -> {
                    if (!isOkDetected.get()) {
                        saveAiMessage(memberId, chatRoomId, prompt.getLevel(), fullAnswer);
                    } else {
                        // 현재 단계가 종료된 경우 && 커플 연동이 되지 않은 멤버의 마지막 프롬프트인 경우
                        if (prompt.isLastPromptForNotCoupleMember() && !isMemberCoupled) {
                            // 채팅방 상태를 PAUSED로 변경하고, SSE 이벤트 전송
                            chatRoomDomainService.updateChatRoomStateToPaused(chatRoomId);
                            sendSseEventPort.sendToMember(
                                    memberId,
                                    new SendSseEventPort.NotificationEvent(
                                            SendSseEventPort.SseEventType.CHAT_ROOM_PAUSED,
                                            "커플 연동 전 대화가 종료되었습니다. 커플 연동을 해주세요."
                                    ));
                        } else {
                            // 다음 단계로 넘어가야 하는 상황
                            sendSseEventPort.sendToMember(
                                    memberId,
                                    new SendSseEventPort.NotificationEvent(
                                            SendSseEventPort.SseEventType.CURRENT_LEVEL_FINISHED,
                                            "현재 단계가 완료되었습니다. upgrade를 요청해주세요."
                                    ));
                        }
                    }
                },
                // 에러 발생 시 에러 메시지 전송
                errorMessage -> sendSseErrorMessage(memberId, errorMessage)
        );

    }

    // Message 요약 API 요청 Async Function
    public void requestSummaryAsync(ChatRoomId chatRoomId, Prompt systemPrompt, Prompt prompt, Prompt summaryPrompt, List<Map<String, String>> summaryRequestMessages) {
        summaryRequestMessages.add(
                createMessageMap(SenderType.SYSTEM, systemPrompt.getContent())
        );
        summaryRequestMessages.add(
                createMessageMap(SenderType.SYSTEM, prompt.getContent())
        );
        summaryRequestMessages.add(
                createMessageMap(SenderType.SYSTEM, "[현재 단계 지시]\n" + summaryPrompt.getContent())
        );

        requestChatApiPort.requestSummary(
                summaryRequestMessages,
                summary -> {
                    // 일반적인 상담 단계인 경우
                    ChatMessageSummary chatMessageSummary = ChatMessageSummary.createChatMessageSummary(
                            chatRoomId, summary, prompt.getLevel()
                    );
                    saveChatMessageSummaryPort.saveChatMessageSummary(chatMessageSummary);
                }
        );

    }

    public void requestTotalSummary(ChatRoom chatRoom, Prompt systemPrompt, Prompt totalSummaryPrompt, List<Map<String, String>> messages) {
        messages.add(
                createMessageMap(SenderType.SYSTEM, systemPrompt.getContent())
        );
        messages.add(
                createMessageMap(SenderType.SYSTEM, "[현재 단계 지시]\n" + totalSummaryPrompt.getContent())
        );

        String summary = requestChatApiPort.requestTotalSummary(messages);

        chatRoomDomainService.updateChatRoomSummary(chatRoom, summary);
    }

    private void saveAiMessage(MemberId memberId, ChatRoomId chatRoomId, int level, String fullAnswer) {
        ChatMessage aiTextMessage = chatMessagesDomainService.createAiTextMessage(chatRoomId, level, fullAnswer);
        sendSseEventPort.sendToMember(
                memberId,
                new SendSseEventPort.NotificationEvent(
                        SendSseEventPort.SseEventType.AI_RESPONSE_ID,
                        aiTextMessage.getId()
                ));
    }

    private void sendSseMessage(MemberId memberId, String chunk) {
        sendSseEventPort.sendToMember(
                memberId,
                new SendSseEventPort.NotificationEvent(
                        SendSseEventPort.SseEventType.CHAT_RESPONSE,
                        chunk
                ));
    }

    private void sendSseErrorMessage(MemberId memberId, String chunk) {
        sendSseEventPort.sendToMember(
                memberId,
                new SendSseEventPort.NotificationEvent(
                        SendSseEventPort.SseEventType.CHAT_RESPONSE,
                        chunk
                ));
    }

    private Map<String, String> createMessageMap(SenderType senderType, String content) {
        return Map.of(
                "role", senderType.getApiName(),
                "content", content
        );
    }
}
