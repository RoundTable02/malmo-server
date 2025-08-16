package makeus.cmc.malmo.application.service.chat;

import lombok.RequiredArgsConstructor;
import makeus.cmc.malmo.application.helper.chat_room.ChatRoomCommandHelper;
import makeus.cmc.malmo.application.helper.chat_room.ChatRoomQueryHelper;
import makeus.cmc.malmo.application.helper.chat_room.PromptQueryHelper;
import makeus.cmc.malmo.application.helper.couple.CoupleQueryHelper;
import makeus.cmc.malmo.application.helper.member.MemberMemoryCommandHelper;
import makeus.cmc.malmo.application.helper.member.MemberQueryHelper;
import makeus.cmc.malmo.application.helper.question.CoupleQuestionQueryHelper;
import makeus.cmc.malmo.application.port.in.chat.ProcessMessageUseCase;
import makeus.cmc.malmo.application.port.out.SendSseEventPort;
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
import makeus.cmc.malmo.domain.value.id.CoupleMemberId;
import makeus.cmc.malmo.domain.value.id.CoupleQuestionId;
import makeus.cmc.malmo.domain.value.id.MemberId;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

@Service
@RequiredArgsConstructor
public class ChatMessageService implements ProcessMessageUseCase {

    private final MemberQueryHelper memberQueryHelper;
    private final ChatRoomQueryHelper chatRoomQueryHelper;
    private final PromptQueryHelper promptQueryHelper;
    private final ChatPromptBuilder chatPromptBuilder;
    private final ChatProcessor chatProcessor;
    private final ValidateMemberPort validateMemberPort;
    private final ChatSseSender chatSseSender;

    private final ChatRoomCommandHelper chatRoomCommandHelper;
    private final ChatRoomDomainService chatRoomDomainService;
    private final SaveChatMessageSummaryPort saveChatMessageSummaryPort;

    private final CoupleQuestionQueryHelper coupleQuestionQueryHelper;

    private final MemberMemoryCommandHelper memberMemoryCommandHelper;

    @Override
    public void processStreamChatMessage(ProcessMessageCommand command) {
        MemberId memberId = MemberId.of(command.getMemberId());
        Member member = memberQueryHelper.getMemberByIdOrThrow(memberId);
        ChatRoom chatRoom = chatRoomQueryHelper.getChatRoomByIdOrThrow(ChatRoomId.of(command.getChatRoomId()));

        List<Map<String, String>> messages = chatPromptBuilder.createForProcessUserMessage(member, chatRoom, command.getNowMessage());

        Prompt systemPrompt = promptQueryHelper.getSystemPrompt();
        Prompt prompt = promptQueryHelper.getPromptByLevel(command.getPromptLevel());
        boolean isMemberCouple = validateMemberPort.isCoupleMember(memberId);

        AtomicBoolean isOkDetected = new AtomicBoolean(false);

        chatProcessor.streamChat(messages, systemPrompt, prompt,
                chunk -> {
                    if (chunk.contains("OK")) {
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
    }

    @Override
    public void processSummary(ProcessSummaryCommand command) {
        ChatRoom chatRoom = chatRoomQueryHelper.getChatRoomByIdOrThrow(ChatRoomId.of(command.getChatRoomId()));
        Prompt systemPrompt = promptQueryHelper.getSystemPrompt();
        Prompt prompt = promptQueryHelper.getPromptByLevel(chatRoom.getLevel());
        Prompt summaryPrompt = promptQueryHelper.getSummaryPrompt();

        // 현재 단계 채팅에 대한 전체 요약 요청
        List<Map<String, String>> summaryMessages = chatPromptBuilder.createForSummaryAsync(chatRoom);
        String summary = chatProcessor.requestSummaryAsync(summaryMessages, systemPrompt, prompt, summaryPrompt);

        ChatMessageSummary chatMessageSummary = ChatMessageSummary.createChatMessageSummary(
                ChatRoomId.of(chatRoom.getId()), summary, prompt.getLevel());

        saveChatMessageSummaryPort.saveChatMessageSummary(chatMessageSummary);
    }

    @Override
    public void processTotalSummary(ProcessTotalSummaryCommand command) {
        ChatRoom chatRoom = chatRoomQueryHelper.getChatRoomByIdOrThrow(ChatRoomId.of(command.getChatRoomId()));

        Prompt systemPrompt = promptQueryHelper.getSystemPrompt();
        Prompt totalSummaryPrompt = promptQueryHelper.getTotalSummaryPrompt();

        List<Map<String, String>> messages = chatPromptBuilder.createForTotalSummary(chatRoom);

        ChatProcessor.CounselingSummary summary = chatProcessor
                .requestTotalSummary(messages, systemPrompt, totalSummaryPrompt);

        chatRoom.updateChatRoomSummary(
                summary.getTotalSummary(),
                summary.getSituationKeyword(),
                summary.getSolutionKeyword()
        );
        chatRoomCommandHelper.saveChatRoom(chatRoom);
    }

    @Override
    public void processAnswerMetadata(ProcessAnswerCommand command) {
        MemberAnswer memberAnswer = coupleQuestionQueryHelper.getMemberAnswerByCoupleMemberId(
                CoupleQuestionId.of(command.getCoupleQuestionId()),
                CoupleMemberId.of(command.getCoupleMemberId()));

        CoupleQuestion coupleQuestion = coupleQuestionQueryHelper.getCoupleQuestionByIdOrThrow(
                CoupleQuestionId.of(command.getCoupleQuestionId()));

        Prompt metadataPrompt = promptQueryHelper.getMemberAnswerMetadata();

        String metadata = chatProcessor.requestMetaData(
                coupleQuestion.getQuestion().getContent(),
                memberAnswer.getAnswer(),
                metadataPrompt
        );

        // 멤버 메모리 생성 및 저장
        MemberMemory memberMemory = MemberMemory.createMemberMemory(
                CoupleMemberId.of(command.getCoupleMemberId()),
                metadata);

        memberMemoryCommandHelper.saveMemberMemory(memberMemory);
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

    private void saveAiMessage(MemberId memberId, ChatRoomId chatRoomId, int level, String fullAnswer) {
        ChatMessage aiTextMessage = chatRoomDomainService.createAiMessage(chatRoomId, level, fullAnswer);
        ChatMessage savedMessage = chatRoomCommandHelper.saveChatMessage(aiTextMessage);
        chatSseSender.sendAiResponseId(memberId, savedMessage.getId());
    }
}
