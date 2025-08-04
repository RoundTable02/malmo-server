package makeus.cmc.malmo.application.service.chat;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import makeus.cmc.malmo.adaptor.in.aop.CheckValidMember;
import makeus.cmc.malmo.application.port.in.chat.SendChatMessageUseCase;
import makeus.cmc.malmo.application.port.out.chat.SaveChatMessageSummaryPort;
import makeus.cmc.malmo.application.port.out.SendSseEventPort;
import makeus.cmc.malmo.application.port.out.member.ValidateMemberPort;
import makeus.cmc.malmo.application.service.helper.chat_room.ChatRoomCommandHelper;
import makeus.cmc.malmo.application.service.helper.chat_room.ChatRoomQueryHelper;
import makeus.cmc.malmo.application.service.helper.chat_room.PromptQueryHelper;
import makeus.cmc.malmo.application.service.helper.member.MemberQueryHelper;
import makeus.cmc.malmo.domain.model.chat.ChatMessage;
import makeus.cmc.malmo.domain.model.chat.ChatMessageSummary;
import makeus.cmc.malmo.domain.model.chat.ChatRoom;
import makeus.cmc.malmo.domain.model.chat.Prompt;
import makeus.cmc.malmo.domain.model.member.Member;
import makeus.cmc.malmo.domain.service.ChatRoomDomainService;
import makeus.cmc.malmo.domain.value.id.ChatRoomId;
import makeus.cmc.malmo.domain.value.id.MemberId;
import makeus.cmc.malmo.domain.value.state.ChatRoomState;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import static makeus.cmc.malmo.util.GlobalConstants.FINAL_MESSAGE;
import static makeus.cmc.malmo.util.GlobalConstants.LAST_PROMPT_LEVEL;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatService implements SendChatMessageUseCase {

    private final ChatRoomDomainService chatRoomDomainService;
    private final PromptQueryHelper promptQueryHelper;
    private final ChatPromptBuilder chatPromptBuilder;
    private final ChatProcessor chatProcessor;
    private final ChatSseSender chatSseSender;
    private final SaveChatMessageSummaryPort saveChatMessageSummaryPort;

    private final ValidateMemberPort validateMemberPort;
    private final MemberQueryHelper memberQueryHelper;
    private final ChatRoomQueryHelper chatRoomQueryHelper;
    private final ChatRoomCommandHelper chatRoomCommandHelper;

    @Override
    @Transactional
    @CheckValidMember
    public SendChatMessageResponse processUserMessage(SendChatMessageCommand command) {
        // 활성화된 채팅방이 있는지 확인
        MemberId memberId = MemberId.of(command.getUserId());
        chatRoomQueryHelper.validateChatRoomAlive(memberId);

        Member member = memberQueryHelper.getMemberByIdOrThrow(memberId);
        ChatRoom chatRoom = chatRoomQueryHelper.getCurrentChatRoomByMemberIdOrThrow(memberId);

        // 채팅방의 상담 단계가 마지막인 경우 동일한 메시지를 반복하여 전송
        if (chatRoom.getLevel() == LAST_PROMPT_LEVEL) {
            return handleLastPrompt(chatRoom, command.getMessage());
        }

        // 채팅방이 초기화되지 않은 상태인 경우 초기화
        if (chatRoom.getChatRoomState() == ChatRoomState.BEFORE_INIT) {
            chatRoom.updateChatRoomStateAlive();
            chatRoomCommandHelper.saveChatRoom(chatRoom);
        }

        // 현재 유저 메시지를 저장
        ChatMessage savedUserMessage = saveUserMessage(chatRoom, command.getMessage());

        // API 요청을 위한 프롬프트 생성
        List<Map<String, String>> messages = chatPromptBuilder.createForProcessUserMessage(member, chatRoom, command.getMessage());

        Prompt systemPrompt = promptQueryHelper.getSystemPrompt();
        Prompt prompt = promptQueryHelper.getPromptByLevel(chatRoom.getLevel());
        boolean isMemberCouple = validateMemberPort.isCoupleMember(memberId);

        AtomicBoolean isOkDetected = new AtomicBoolean(false);

        chatProcessor.streamChat(messages, systemPrompt, prompt,
                chunk -> {
                    if (chunk.contains("OK") && !prompt.isLastPrompt()) {
                        // OK 응답이 감지되면 isOkDetected를 true로 설정
                        isOkDetected.set(true);
                    } else {
                        // OK가 감지되지 않은 경우 SSE 응답 스트리밍
                        chatSseSender.sendResponseChunk(memberId, chunk);
                    }
                },
                fullAnswer -> {
                    if (!isOkDetected.get()) {
                        // OK가 감지되지 않은 경우 전체 응답을 저장
                        saveAiMessage(memberId, ChatRoomId.of(chatRoom.getId()), prompt.getLevel(), fullAnswer);
                    } else {
                        // OK가 감지된 경우 OK를 제거하고 저장 후 현재 레벨 완료 처리
                        fullAnswer = fullAnswer.replace("OK", "").trim();
                        saveAiMessage(memberId, ChatRoomId.of(chatRoom.getId()), prompt.getLevel(), fullAnswer);
                        handleLevelFinished(memberId, ChatRoomId.of(chatRoom.getId()), prompt, isMemberCouple);
                    }
                },
                errorMessage -> chatSseSender.sendError(memberId, errorMessage)
        );

        return SendChatMessageResponse.builder()
                .messageId(savedUserMessage.getId())
                .build();
    }

    @Override
    @Transactional
    @CheckValidMember
    public void upgradeChatRoom(SendChatMessageCommand command) {
        // 채팅방의 현재 상담 단계가 완료된 상테 -> 다음 단계로 업그레이드
        MemberId memberId = MemberId.of(command.getUserId());
        ChatRoom chatRoom = chatRoomQueryHelper.getCurrentChatRoomByMemberIdOrThrow(memberId);
        int nowChatRoomLevel = chatRoom.getLevel();

        chatRoom.upgradeChatRoom();
        chatRoomCommandHelper.saveChatRoom(chatRoom);

        Prompt systemPrompt = promptQueryHelper.getSystemPrompt();
        Prompt prompt = promptQueryHelper.getPromptByLevel(nowChatRoomLevel);
        Prompt summaryPrompt = promptQueryHelper.getSummaryPrompt();
        Prompt nextPrompt = promptQueryHelper.getPromptByLevel(nowChatRoomLevel + 1);

        // 현재 단계 채팅에 대한 전체 요약 요청 (비동기)
        List<Map<String, String>> summaryMessages = chatPromptBuilder.createForSummaryAsync(chatRoom);
        chatProcessor.requestSummaryAsync(summaryMessages, systemPrompt, prompt, summaryPrompt,
                summary -> {
                    ChatMessageSummary chatMessageSummary = ChatMessageSummary.createChatMessageSummary(
                            ChatRoomId.of(chatRoom.getId()), summary, prompt.getLevel()
                    );
                    // 요약된 메시지 저장
                    saveChatMessageSummaryPort.saveChatMessageSummary(chatMessageSummary);
                }
        );

        // 다음 단계 오프닝 멘트 요청
        List<Map<String, String>> openingMessages = chatPromptBuilder.createForNextLevelOpening(chatRoom);

        // 오프닝 멘트에 대한 AI 응답 SSE 스트리밍
        chatProcessor.streamChat(openingMessages, systemPrompt, nextPrompt,
                chunk -> chatSseSender.sendResponseChunk(memberId, chunk),
                fullAnswer -> saveAiMessage(memberId, ChatRoomId.of(chatRoom.getId()), nextPrompt.getLevel(), fullAnswer),
                errorMessage -> chatSseSender.sendError(memberId, errorMessage)
        );

        // 다음 단계 상담 도달, 채팅방 활성화
        chatRoom.updateChatRoomStateAlive();
        chatRoomCommandHelper.saveChatRoom(chatRoom);
    }

    /*
    - 1단계의 경우 커플 연동이 되지 않은 상태에서 대화가 종료되면 채팅방 상태를 일시정지로 변경하고 커플 연동을 요청
    - 2단계 이상에서는 현재 단계가 완료되었다는 메시지를 전송
     */
    private void handleLevelFinished(MemberId memberId, ChatRoomId chatRoomId, Prompt prompt, boolean isMemberCoupled) {
        if (prompt.isLastPromptForNotCoupleMember() && !isMemberCoupled) {
            // 커플 연동이 되지 않은 상태에서 1단계가 종료된 경우
            // 채팅방 상태를 일시정지로 변경하고 커플 연동 요청
            ChatRoom chatRoom = chatRoomQueryHelper.getChatRoomByIdOrThrow(chatRoomId);
            chatRoom.updateChatRoomStatePaused();
            chatRoomCommandHelper.saveChatRoom(chatRoom);
            chatSseSender.sendFlowEvent(
                    memberId,
                    SendSseEventPort.SseEventType.CHAT_ROOM_PAUSED,
                    "커플 연동 전 대화가 종료되었습니다. 커플 연동을 해주세요."
            );
        } else {
            // 2단계 이상이거나 커플 연동이 된 상태에서 현재 단계가 완료된 경우
            // 현재 단계가 완료되었다는 메시지를 전송
            chatSseSender.sendFlowEvent(
                    memberId,
                    SendSseEventPort.SseEventType.CURRENT_LEVEL_FINISHED,
                    "현재 단계가 완료되었습니다. upgrade를 요청해주세요."
            );
        }
    }

    private SendChatMessageResponse handleLastPrompt(ChatRoom chatRoom, String message) {
        // 마지막 단계에서 고정된 메시지를 반복하여 전송
        ChatMessage userMessage = saveUserMessage(chatRoom, message);
        chatSseSender.sendLastResponse(chatRoom.getMemberId(), FINAL_MESSAGE);
        saveAiMessage(chatRoom.getMemberId(), ChatRoomId.of(chatRoom.getId()), LAST_PROMPT_LEVEL, FINAL_MESSAGE);
        return SendChatMessageResponse.builder()
                .messageId(userMessage.getId())
                .build();
    }

    private ChatMessage saveUserMessage(ChatRoom chatRoom, String message) {
        ChatMessage userMessage = chatRoomDomainService.createUserMessage(ChatRoomId.of(chatRoom.getId()), chatRoom.getLevel(), message);
        return chatRoomCommandHelper.saveChatMessage(userMessage);
    }

    private void saveAiMessage(MemberId memberId, ChatRoomId chatRoomId, int level, String fullAnswer) {
        ChatMessage aiTextMessage = chatRoomDomainService.createAiMessage(chatRoomId, level, fullAnswer);
        ChatMessage savedMessage = chatRoomCommandHelper.saveChatMessage(aiTextMessage);
        chatSseSender.sendAiResponseId(memberId, savedMessage.getId());
    }
}
