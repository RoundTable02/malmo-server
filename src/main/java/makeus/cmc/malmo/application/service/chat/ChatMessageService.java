package makeus.cmc.malmo.application.service.chat;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import makeus.cmc.malmo.application.helper.chat_room.ChatRoomCommandHelper;
import makeus.cmc.malmo.application.helper.chat_room.ChatRoomQueryHelper;
import makeus.cmc.malmo.application.helper.chat_room.PromptQueryHelper;
import makeus.cmc.malmo.application.helper.member.MemberMemoryCommandHelper;
import makeus.cmc.malmo.application.helper.member.MemberQueryHelper;
import makeus.cmc.malmo.application.helper.question.CoupleQuestionQueryHelper;
import makeus.cmc.malmo.application.port.in.chat.ProcessMessageUseCase;
import makeus.cmc.malmo.application.port.out.sse.SendSseEventPort;
import makeus.cmc.malmo.application.port.out.chat.SaveChatMessageSummaryPort;
import makeus.cmc.malmo.application.port.out.member.ValidateMemberPort;
import makeus.cmc.malmo.domain.model.chat.ChatMessage;
import makeus.cmc.malmo.domain.model.chat.ChatMessageSummary;
import makeus.cmc.malmo.domain.model.chat.ChatRoom;
import makeus.cmc.malmo.domain.model.chat.Prompt;
import makeus.cmc.malmo.domain.model.member.Member;
import makeus.cmc.malmo.domain.model.member.MemberMemory;
import makeus.cmc.malmo.domain.model.question.CoupleQuestion;
import makeus.cmc.malmo.domain.model.question.MemberAnswer;
import makeus.cmc.malmo.domain.service.ChatRoomDomainService;
import makeus.cmc.malmo.domain.value.id.ChatRoomId;
import makeus.cmc.malmo.domain.value.id.CoupleId;
import makeus.cmc.malmo.domain.value.id.CoupleQuestionId;
import makeus.cmc.malmo.domain.value.id.MemberId;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatMessageService implements ProcessMessageUseCase {

    private final MemberQueryHelper memberQueryHelper;
    private final ChatRoomQueryHelper chatRoomQueryHelper;
    private final PromptQueryHelper promptQueryHelper;
    private final ChatPromptBuilder chatPromptBuilder;
    private final ChatProcessor chatProcessor;
//    private final ValidateMemberPort validateMemberPort;
    private final ChatSseSender chatSseSender;

    private final ChatRoomCommandHelper chatRoomCommandHelper;
    private final ChatRoomDomainService chatRoomDomainService;
    private final SaveChatMessageSummaryPort saveChatMessageSummaryPort;

    private final CoupleQuestionQueryHelper coupleQuestionQueryHelper;

    private final MemberMemoryCommandHelper memberMemoryCommandHelper;

    @Override
    public CompletableFuture<Void> processStreamChatMessage(ProcessMessageCommand command) {
        MemberId memberId = MemberId.of(command.getMemberId());
        Member member = memberQueryHelper.getMemberByIdOrThrow(memberId);
        ChatRoom chatRoom = chatRoomQueryHelper.getChatRoomByIdOrThrow(ChatRoomId.of(command.getChatRoomId()));

        List<Map<String, String>> messages = chatPromptBuilder.createForProcessUserMessage(member, chatRoom, command.getNowMessage());

        Prompt systemPrompt = promptQueryHelper.getSystemPrompt();
        Prompt prompt = promptQueryHelper.getPromptByLevel(command.getPromptLevel());
        /* 상담 지속 조건: 커플 연동이 있는 경우 */
//        boolean isMemberCouple = validateMemberPort.isCoupleMember(memberId);
        boolean isMemberCouple = true;

        AtomicBoolean isOkDetected = new AtomicBoolean(false);

        return chatProcessor.streamChat(messages, systemPrompt, prompt,
                chunk -> {
                    // 실시간 SSE 전송 로직 (onChunk)
                    if (chunk.contains("OK")) {
                        isOkDetected.set(true);
                    } else {
                        chatSseSender.sendResponseChunk(memberId, chunk);
                    }
                },
                fullAnswer -> {
                    // 스트림 완료 후 DB 저장 및 후처리 로직 (onComplete)
                    if (!isOkDetected.get()) {
                        saveAiMessage(memberId, ChatRoomId.of(chatRoom.getId()), prompt.getLevel(), fullAnswer);
                    } else {
                        fullAnswer = fullAnswer.replace("OK", "").trim();
                        saveAiMessage(memberId, ChatRoomId.of(chatRoom.getId()), prompt.getLevel(), fullAnswer);
                        handleLevelFinished(memberId, ChatRoomId.of(chatRoom.getId()), prompt, isMemberCouple);
                    }
                },
                errorMessage -> chatSseSender.sendError(memberId, errorMessage)
        ).toFuture();
    }

    @Override
    public CompletableFuture<Void> processSummary(ProcessSummaryCommand command) {
        ChatRoom chatRoom = chatRoomQueryHelper.getChatRoomByIdOrThrow(ChatRoomId.of(command.getChatRoomId()));
        Prompt systemPrompt = promptQueryHelper.getSystemPrompt();
        Prompt prompt = promptQueryHelper.getPromptByLevel(chatRoom.getLevel());
        Prompt summaryPrompt = promptQueryHelper.getSummaryPrompt();

        // 현재 단계 채팅에 대한 전체 요약 요청
        List<Map<String, String>> summaryMessages = chatPromptBuilder.createForSummaryAsync(chatRoom);
        return chatProcessor.requestSummaryAsync(summaryMessages, systemPrompt, prompt, summaryPrompt)
                .thenAcceptAsync(summary -> { // 비동기 작업이 끝나면 이 블록이 실행됨
                    ChatMessageSummary chatMessageSummary = ChatMessageSummary.createChatMessageSummary(
                            ChatRoomId.of(chatRoom.getId()), summary, prompt.getLevel());
                    saveChatMessageSummaryPort.saveChatMessageSummary(chatMessageSummary);
                    log.info("Successfully processed and saved summary for chatRoomId: {}", command.getChatRoomId());
                }); // 별도의 스레드 풀에서 실행되도록 thenAcceptAsync 사용
    }

    @Override
    public CompletableFuture<Void> processTotalSummary(ProcessTotalSummaryCommand command) {
        ChatRoom chatRoom = chatRoomQueryHelper.getChatRoomByIdOrThrow(ChatRoomId.of(command.getChatRoomId()));

        Prompt systemPrompt = promptQueryHelper.getSystemPrompt();
        Prompt totalSummaryPrompt = promptQueryHelper.getTotalSummaryPrompt();

        List<Map<String, String>> messages = chatPromptBuilder.createForTotalSummary(chatRoom);

        return chatProcessor.requestTotalSummary(messages, systemPrompt, totalSummaryPrompt)
                .thenAcceptAsync(summary -> { // CounselingSummary 객체를 받음
                    chatRoom.updateChatRoomSummary(
                            summary.getTotalSummary(),
                            summary.getSituationKeyword(),
                            summary.getSolutionKeyword()
                    );
                    chatRoomCommandHelper.saveChatRoom(chatRoom);
                    log.info("Successfully processed and saved total summary for chatRoomId: {}", command.getChatRoomId());
                });
    }

    @Override
    public CompletableFuture<Void> processAnswerMetadata(ProcessAnswerCommand command) {
        // 1. 비동기 작업에 필요한 데이터는 미리 동기적으로 조회합니다.
        MemberAnswer memberAnswer = coupleQuestionQueryHelper.getMemberAnswerOrThrow(
                CoupleQuestionId.of(command.getCoupleQuestionId()),
                MemberId.of(command.getMemberId()));

        CoupleQuestion coupleQuestion = coupleQuestionQueryHelper.getCoupleQuestionByIdOrThrow(
                CoupleQuestionId.of(command.getCoupleQuestionId()));

        Prompt metadataPrompt = promptQueryHelper.getMemberAnswerMetadata();

        // 2. 비동기 API 호출로 CompletableFuture 체인을 시작하고, 그 결과를 즉시 반환합니다.
        return chatProcessor.requestMetaData(
                        coupleQuestion.getQuestion().getContent(),
                        memberAnswer.getAnswer(),
                        metadataPrompt
                )
                // 3. API 호출이 성공적으로 완료되면(then), 그 결과(metadata)를 받아서(Accept) 다음 작업을 비동기(Async)로 실행합니다.
                .thenAcceptAsync(metadata -> {
                    // 멤버 메모리 생성 및 저장
                    MemberMemory memberMemory = MemberMemory.createMemberMemory(
                            CoupleId.of(command.getCoupleId()),
                            MemberId.of(command.getMemberId()),
                            metadata);

                    memberMemoryCommandHelper.saveMemberMemory(memberMemory);
                });
    }

    /*
    - 1단계의 경우 커플 연동이 되지 않은 상태에서 대화가 종료되면 채팅방 상태를 일시정지로 변경하고 커플 연동을 요청
    - 2단계 이상에서는 현재 단계가 완료되었다는 메시지를 전송
     */
    private void handleLevelFinished(MemberId memberId, ChatRoomId chatRoomId, Prompt prompt, boolean isMemberCoupled) {
        /* 상담 지속 조건: 커플 연동이 있는 경우 */
//        if (prompt.isLastPromptForNotCoupleMember() && !isMemberCoupled) {
//            // 커플 연동이 되지 않은 상태에서 1단계가 종료된 경우
//            // 채팅방 상태를 일시정지로 변경하고 커플 연동 요청
//            ChatRoom chatRoom = chatRoomQueryHelper.getChatRoomByIdOrThrow(chatRoomId);
//            chatRoom.updateChatRoomStatePaused();
//            chatRoomCommandHelper.saveChatRoom(chatRoom);
//            chatSseSender.sendFlowEvent(
//                    memberId,
//                    SendSseEventPort.SseEventType.CHAT_ROOM_PAUSED,
//                    "커플 연동 전 대화가 종료되었습니다. 커플 연동을 해주세요."
//            );
//        } else {
//            // 2단계 이상이거나 커플 연동이 된 상태에서 현재 단계가 완료된 경우
//            // 현재 단계가 완료되었다는 메시지를 전송
//        }

        chatSseSender.sendFlowEvent(
                memberId,
                SendSseEventPort.SseEventType.CURRENT_LEVEL_FINISHED,
                "현재 단계가 완료되었습니다. upgrade를 요청해주세요."
        );
    }

    private void saveAiMessage(MemberId memberId, ChatRoomId chatRoomId, int level, String fullAnswer) {
        ChatMessage aiTextMessage = chatRoomDomainService.createAiMessage(chatRoomId, level, fullAnswer);
        ChatMessage savedMessage = chatRoomCommandHelper.saveChatMessage(aiTextMessage);
        chatSseSender.sendAiResponseId(memberId, savedMessage.getId());
    }
}
