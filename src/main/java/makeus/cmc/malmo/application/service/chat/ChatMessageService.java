package makeus.cmc.malmo.application.service.chat;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import makeus.cmc.malmo.application.helper.chat_room.ChatRoomCommandHelper;
import makeus.cmc.malmo.application.helper.chat_room.ChatRoomQueryHelper;
import makeus.cmc.malmo.application.helper.chat_room.PromptQueryHelper;
import makeus.cmc.malmo.application.helper.chat_room.DetailedPromptQueryHelper;
import makeus.cmc.malmo.application.helper.chat_room.MemberChatRoomMetadataQueryHelper;
import makeus.cmc.malmo.application.helper.chat_room.MemberChatRoomMetadataCommandHelper;
import makeus.cmc.malmo.application.helper.member.MemberMemoryCommandHelper;
import makeus.cmc.malmo.application.helper.member.MemberQueryHelper;
import makeus.cmc.malmo.application.helper.question.CoupleQuestionQueryHelper;
import makeus.cmc.malmo.application.port.in.chat.ProcessMessageUseCase;
import makeus.cmc.malmo.application.port.in.chat.SufficiencyCheckResult;
import makeus.cmc.malmo.application.port.out.sse.SendSseEventPort;
import makeus.cmc.malmo.application.port.out.chat.SaveChatMessageSummaryPort;
import makeus.cmc.malmo.domain.model.chat.ChatMessage;
import makeus.cmc.malmo.domain.model.chat.ChatMessageSummary;
import makeus.cmc.malmo.domain.model.chat.ChatRoom;
import makeus.cmc.malmo.domain.model.chat.Prompt;
import makeus.cmc.malmo.domain.model.chat.DetailedPrompt;
import makeus.cmc.malmo.domain.model.chat.MemberChatRoomMetadata;
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

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatMessageService implements ProcessMessageUseCase {

    private final MemberQueryHelper memberQueryHelper;
    private final ChatRoomQueryHelper chatRoomQueryHelper;
    private final PromptQueryHelper promptQueryHelper;
    private final DetailedPromptQueryHelper detailedPromptQueryHelper;
    private final MemberChatRoomMetadataCommandHelper memberChatRoomMetadataCommandHelper;
    private final ChatPromptBuilder chatPromptBuilder;
    private final ChatProcessor chatProcessor;
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

        // 1. 유저 메시지 저장
        saveUserMessage(chatRoom, command);

        // 2. 충분성 조건 검사
        CompletableFuture<SufficiencyCheckResult> sufficiencyCheck = 
            requestSufficiencyCheck(member, chatRoom, command);

        return sufficiencyCheck.thenCompose(result -> {
            if (!result.isCompleted()) {
                // 조건 미충족: advice 포함하여 응답 생성
                return requestResponseToMeetCondition(member, chatRoom, command, result.getAdvice());
            }
            
            // 조건 충족: MemberChatRoomMetadata 저장 + 요약 요청
            saveMemberChatRoomMetadata(chatRoom, command, result);

            DetailedPrompt detailedPrompt = detailedPromptQueryHelper.getGuidelinePrompt(
                command.getPromptLevel(), command.getDetailedLevel())
                .orElseThrow(() -> new RuntimeException("Guideline prompt not found"));

            // 마지막 충분성 조건이 아닌 경우
            if (!detailedPrompt.isLastDetailedPrompt()) {
                // 다음 충분성 조건으로
                chatRoom.upgradeDetailedLevel();
                chatRoomCommandHelper.saveChatRoom(chatRoom);
                return requestNextDetailedPromptOpening(chatRoom, command);
            }

            // 마지막 충분성 조건인 경우
            // 단계 요약 요청 (비동기)
            requestStageSummaryAsync(chatRoom, command);

            // 다음 단계 오프닝 생성 요청
            chatRoom.upgradeToNextStage();
            chatRoomCommandHelper.saveChatRoom(chatRoom);
            return requestNextStageOpening(member, chatRoom, command);
        });
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
                            summary.getSolutionKeyword(),
                            summary.getCounselingType()
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

        Prompt metadataPrompt = promptQueryHelper.getAnswerMetadataPrompt();

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

    private void saveUserMessage(ChatRoom chatRoom, ProcessMessageCommand command) {
        ChatMessage userMessage = chatRoomDomainService.createUserMessage(
                ChatRoomId.of(chatRoom.getId()), 
                command.getPromptLevel(), 
                command.getDetailedLevel(),
                command.getNowMessage()
        );
        chatRoomCommandHelper.saveChatMessage(userMessage);
    }

    private CompletableFuture<SufficiencyCheckResult> requestSufficiencyCheck(Member member, ChatRoom chatRoom, ProcessMessageCommand command) {
        List<Map<String, String>> messages = chatPromptBuilder.createForSufficiencyCheck(
                member, chatRoom, command.getPromptLevel(), command.getDetailedLevel());
        
        log.info("Requesting sufficiency check for memberId: {}, chatRoomId: {}, level: {}, detailedLevel: {}",
                member.getId(), chatRoom.getId(), command.getPromptLevel(), command.getDetailedLevel());
        DetailedPrompt validationPrompt = detailedPromptQueryHelper.getValidationPrompt(
                command.getPromptLevel(), command.getDetailedLevel())
                .orElseThrow(() -> new RuntimeException("Validation prompt not found"));
        
        return chatProcessor.requestSufficiencyCheck(messages, validationPrompt);
    }

    private CompletableFuture<Void> requestResponseToMeetCondition(Member member, ChatRoom chatRoom, ProcessMessageCommand command, String advice) {
        List<Map<String, String>> messages = chatPromptBuilder.createForProcessUserMessage(
                member, chatRoom, command.getNowMessage());
        
        // advice를 포함한 프롬프트 추가
        if (advice != null && !advice.isEmpty()) {
            messages.add(Map.of("role", "system", "content", "현재 사용자의 상황 정보가 부족합니다. " + advice));
        }
        
        Prompt systemPrompt = promptQueryHelper.getSystemPrompt();
        Prompt prompt = promptQueryHelper.getGuidelinePrompt(command.getPromptLevel());
        DetailedPrompt detailedPrompt = detailedPromptQueryHelper.getGuidelinePrompt(
                        command.getPromptLevel(), command.getDetailedLevel())
                .orElseThrow(() -> new RuntimeException("Guideline prompt not found"));
        
        return chatProcessor.streamChat(messages, systemPrompt, prompt, detailedPrompt,
                chunk -> chatSseSender.sendResponseChunk(MemberId.of(member.getId()), chunk),
                fullAnswer -> saveAiMessage(MemberId.of(member.getId()), ChatRoomId.of(chatRoom.getId()),
                        command.getPromptLevel(), command.getDetailedLevel(), fullAnswer),
                errorMessage -> chatSseSender.sendError(MemberId.of(member.getId()), errorMessage)
        ).toFuture();
    }

    private void saveMemberChatRoomMetadata(ChatRoom chatRoom, ProcessMessageCommand command, SufficiencyCheckResult result) {
        DetailedPrompt detailedPrompt = detailedPromptQueryHelper.getGuidelinePrompt(
            command.getPromptLevel(), command.getDetailedLevel())
            .orElseThrow(() -> new RuntimeException("Guideline prompt not found"));
        MemberChatRoomMetadata metadata = MemberChatRoomMetadata.create(
                ChatRoomId.of(chatRoom.getId()),
                MemberId.of(command.getMemberId()),
                command.getPromptLevel(),
                command.getDetailedLevel(),
                detailedPrompt.getMetadataTitle(),
                result.getSummary()
        );
        memberChatRoomMetadataCommandHelper.saveMemberChatRoomMetadata(metadata);
    }

    private CompletableFuture<Void> requestNextDetailedPromptOpening(ChatRoom chatRoom, ProcessMessageCommand command) {
        // 다음 충분성 조건 오프닝 생성 요청
        MemberId memberId = MemberId.of(command.getMemberId());
        Member member = memberQueryHelper.getMemberByIdOrThrow(memberId);

        // 사용자 메타데이터 정보 생성
        List<Map<String, String>> messages = chatPromptBuilder.createForNextDetailedPrompt(
                member, chatRoom, command.getPromptLevel(), command.getDetailedLevel() + 1);

        // 시스템 프롬프트 + 현재 단계 프롬프트 + 다음 충분성 조건 프롬프트
        Prompt systemPrompt = promptQueryHelper.getSystemPrompt();
        Prompt prompt = promptQueryHelper.getGuidelinePrompt(chatRoom.getLevel());
        DetailedPrompt nextDetailedPrompt = detailedPromptQueryHelper.getGuidelinePrompt(
                command.getPromptLevel(), command.getDetailedLevel() + 1)
                .orElseThrow(() -> new RuntimeException("Next guideline prompt not found"));
        
        return chatProcessor.streamChat(messages, systemPrompt, prompt, nextDetailedPrompt,
                chunk -> chatSseSender.sendResponseChunk(memberId, chunk),
                fullAnswer -> saveAiMessage(memberId, ChatRoomId.of(chatRoom.getId()), 
                        command.getPromptLevel(), command.getDetailedLevel() + 1, fullAnswer),
                errorMessage -> chatSseSender.sendError(memberId, errorMessage)
        ).toFuture();
    }

    private void requestStageSummaryAsync(ChatRoom chatRoom, ProcessMessageCommand command) {
        List<Map<String, String>> messages = chatPromptBuilder.createForStageSummary(
                chatRoom, command.getPromptLevel());
        
        Prompt systemPrompt = promptQueryHelper.getSystemPrompt();
        Prompt prompt = promptQueryHelper.getGuidelinePrompt(command.getPromptLevel());
        Prompt summaryPrompt = promptQueryHelper.getSummaryPrompt(command.getPromptLevel());
        
        chatProcessor.requestStageSummary(messages, systemPrompt, prompt, summaryPrompt)
                .thenAcceptAsync(summary -> {
                    ChatMessageSummary chatMessageSummary = ChatMessageSummary.createChatMessageSummary(
                            ChatRoomId.of(chatRoom.getId()), summary, command.getPromptLevel());
                    saveChatMessageSummaryPort.saveChatMessageSummary(chatMessageSummary);
                    log.info("Stage summary completed for chatRoomId: {}, level: {}", 
                            chatRoom.getId(), command.getPromptLevel());
                });
    }

    private CompletableFuture<Void> requestNextStageOpening(Member member, ChatRoom chatRoom, ProcessMessageCommand command) {
        List<Map<String, String>> messages = chatPromptBuilder.createForNextStage(
                member, chatRoom, command.getPromptLevel() + 1);
        
        Prompt systemPrompt = promptQueryHelper.getSystemPrompt();
        Prompt nextPrompt = promptQueryHelper.getGuidelinePrompt(command.getPromptLevel() + 1);

        if (nextPrompt.isForCompletedResponse()) {
            String finalMessage = nextPrompt.getContent();
            saveAiMessage(MemberId.of(member.getId()), ChatRoomId.of(chatRoom.getId()),
                    command.getPromptLevel(), command.getDetailedLevel(), finalMessage);

            return CompletableFuture.completedFuture(null);
        }

        DetailedPrompt nextDetailedPrompt = detailedPromptQueryHelper.getGuidelinePrompt(
                        command.getPromptLevel() + 1, 1)
                .orElseThrow(() -> new RuntimeException("Next stage guideline prompt not found"));
        
        return chatProcessor.streamChat(messages, systemPrompt, nextPrompt, nextDetailedPrompt,
                chunk -> chatSseSender.sendResponseChunk(MemberId.of(member.getId()), chunk),
                fullAnswer -> saveAiMessage(MemberId.of(member.getId()), ChatRoomId.of(chatRoom.getId()),
                        command.getPromptLevel() + 1, 1, fullAnswer),
                errorMessage -> chatSseSender.sendError(MemberId.of(member.getId()), errorMessage)
        ).toFuture();
    }

    private void saveAiMessage(MemberId memberId, ChatRoomId chatRoomId, int level, int detailedLevel, String fullAnswer) {
        ChatMessage aiTextMessage = chatRoomDomainService.createAiMessage(chatRoomId, level, detailedLevel, fullAnswer);
        ChatMessage savedMessage = chatRoomCommandHelper.saveChatMessage(aiTextMessage);
        chatSseSender.sendAiResponseId(memberId, savedMessage.getId());
    }
}
