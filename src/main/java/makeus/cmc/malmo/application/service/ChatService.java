package makeus.cmc.malmo.application.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import makeus.cmc.malmo.adaptor.in.aop.CheckValidMember;
import makeus.cmc.malmo.application.port.in.SendChatMessageUseCase;
import makeus.cmc.malmo.application.port.out.LoadChatRoomMetadataPort;
import makeus.cmc.malmo.application.port.out.ValidateMemberPort;
import makeus.cmc.malmo.domain.model.chat.ChatMessage;
import makeus.cmc.malmo.domain.model.chat.ChatMessageSummary;
import makeus.cmc.malmo.domain.model.chat.ChatRoom;
import makeus.cmc.malmo.domain.model.chat.Prompt;
import makeus.cmc.malmo.domain.model.member.Member;
import makeus.cmc.malmo.domain.service.*;
import makeus.cmc.malmo.domain.value.id.ChatRoomId;
import makeus.cmc.malmo.domain.value.id.MemberId;
import makeus.cmc.malmo.domain.value.state.ChatRoomState;
import makeus.cmc.malmo.domain.value.type.SenderType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ChatService implements SendChatMessageUseCase {

    private final ChatRoomDomainService chatRoomDomainService;
    private final ChatMessagesDomainService chatMessagesDomainService;
    private final MemberDomainService memberDomainService;
    private final ChatStreamProcessor chatStreamProcessor;
    private final MemberMemoryDomainService memberMemoryDomainService;

    private final ValidateMemberPort validateMemberPort;
    private final PromptDomainService promptDomainService;

    @Override
    @Transactional
    @CheckValidMember
    public SendChatMessageResponse processUserMessage(SendChatMessageCommand command) {
        chatRoomDomainService.validateChatRoomAlive(MemberId.of(command.getUserId()));

        List<Map<String, String>> messages = new ArrayList<>();

        Member member = memberDomainService.getMemberById(MemberId.of(command.getUserId()));

        // Member의 닉네임, 디데이, 애착 유형, 상대방 애착 유형 정보 가져오기. (user : [사용자 메타데이터])
        //  D-day 정보는 단기, 중기, 장기로 구분하여 활용
        String metaDataContent = getMetaDataContent(member);

        messages.add(
                createMessageMap(SenderType.USER, metaDataContent)
        );

        //  현재 ChatRoom의 LEVEL 불러오기
        ChatRoom chatRoom = chatRoomDomainService.getCurrentChatRoomByMemberId(MemberId.of(member.getId()));

        if (chatRoom.getChatRoomState() == ChatRoomState.BEFORE_INIT) {
            chatRoomDomainService.updateChatRoomStateToAlive(ChatRoomId.of(chatRoom.getId()));
        }

        int chatRoomLevel = chatRoom.getLevel();

        //  시스템 프롬프트 불러오기 (system)
        Prompt systemPrompt = promptDomainService.getSystemPrompt();

        //  LEVEL에 따라 프롬프트 불러오기 (user : [현재 단계 지시])
        Prompt prompt = promptDomainService.getPromptByLevel(chatRoomLevel);

        // 이전 단계 요약본 (system : [이전 단계 요약])
        List<ChatMessageSummary> previousLevelsSummarizedMessages = chatMessagesDomainService.getSummarizedMessages(ChatRoomId.of(chatRoom.getId()));
        String summarizedMessageContent = getSummarizedMessageContent(previousLevelsSummarizedMessages);
        messages.add(
                createMessageMap(SenderType.SYSTEM, summarizedMessageContent)
        );

        List<ChatMessage> currentChatRoomMessages = chatMessagesDomainService.getChatRoomLevelMessages(ChatRoomId.of(chatRoom.getId()), chatRoomLevel);

        // 현재 단계 메시지들
        for (ChatMessage chatMessage : currentChatRoomMessages) {
            messages.add(
                    createMessageMap(chatMessage.getSenderType(), chatMessage.getContent())
            );
        }

        // 현재 메시지 추가
        messages.add(
                createMessageMap(SenderType.USER, command.getMessage())
        );
        ChatMessage savedUserTextMessage = chatMessagesDomainService.createUserTextMessage(ChatRoomId.of(chatRoom.getId()), chatRoomLevel, command.getMessage());

        boolean isMemberCouple = validateMemberPort.isCoupleMember(MemberId.of(member.getId()));
        // OpenAI API 스트리밍 호출
        chatStreamProcessor.requestApiStream(
                MemberId.of(command.getUserId()),
                isMemberCouple,
                systemPrompt,
                prompt,
                messages,
                ChatRoomId.of(chatRoom.getId()));

        return SendChatMessageResponse.builder()
                .messageId(savedUserTextMessage.getId())
                .build();

    }

    @Override
    @Transactional
    @CheckValidMember
    public void upgradeChatRoom(SendChatMessageCommand command) {
        // 이전 LEVEL이 종료, 현재 레벨에 처음 진입한 경우
        //  이전 레벨의 ChatMessageSummary와 summarized = false인 ChatMessage를 비동기 요약 처리
        //  요약 전 메시지를 바탕으로 현재 단계 지시 프롬프트로 요청 (status를 다시 ALIVE로 변경)
        List<Map<String, String>> messages = new ArrayList<>();

        Member member = memberDomainService.getMemberById(MemberId.of(command.getUserId()));
        ChatRoom chatRoom = chatRoomDomainService.getCurrentChatRoomByMemberId(MemberId.of(member.getId()));
        int nowChatRoomLevel = chatRoom.getLevel();

        //  시스템 프롬프트 불러오기 (system)
        Prompt systemPrompt = promptDomainService.getSystemPrompt();
        //  LEVEL에 따라 프롬프트 불러오기 (user : [현재 단계 지시])
        Prompt prompt = promptDomainService.getPromptByLevel(nowChatRoomLevel);

        // 요약 요청 프롬프트 생성
        // 채팅방 상태를 NEED_NEXT_QUESTION로 변경 (다음 단계 질문이 도착하면 다시 ALIVE로 변경)
        chatRoomDomainService.updateChatRoomStateToNeedNextQuestion(ChatRoomId.of(chatRoom.getId()));

        Prompt nextPrompt = promptDomainService.getPromptByLevel(nowChatRoomLevel + 1);

        // 현재 단계 메시지들 불러오기
        List<ChatMessage> currentChatRoomMessages = chatMessagesDomainService.getChatRoomLevelMessages(ChatRoomId.of(chatRoom.getId()), nowChatRoomLevel);
        // 요약된 이전 단계 메시지들 불러오기 (요약에서는 사용X, 비동기 간섭 방지를 위해 미리 Load)
        List<ChatMessageSummary> summarizedMessages = chatMessagesDomainService.getSummarizedMessages(ChatRoomId.of(chatRoom.getId()));

        // 현재 단계 메시지들
        for (ChatMessage chatMessage : currentChatRoomMessages) {
            messages.add(
                    createMessageMap(chatMessage.getSenderType(), chatMessage.getContent())
            );
        }

        // 요약을 위한 프롬프트 불러오기
        Prompt summaryPrompt = promptDomainService.getSummaryPrompt();

        // 요약 요청 (비동기)
        chatStreamProcessor.requestSummaryAsync(
                ChatRoomId.of(chatRoom.getId()),
                systemPrompt, prompt, summaryPrompt,
                messages
        );

        String summarizedMessageContent = getSummarizedMessageContent(summarizedMessages);

        messages.add(
                createMessageMap(SenderType.USER, summarizedMessageContent)
        );

        boolean isMemberCouple = validateMemberPort.isCoupleMember(MemberId.of(member.getId()));
        // 과거 대화로부터 현재 단계 오프닝 멘트 요청
        chatStreamProcessor.requestApiStream(
                MemberId.of(command.getUserId()),
                isMemberCouple,
                systemPrompt,
                nextPrompt,
                messages,
                ChatRoomId.of(chatRoom.getId()));

        chatRoomDomainService.updateChatRoomStateToAlive(ChatRoomId.of(chatRoom.getId()));
    }

    private String getSummarizedMessageContent(List<ChatMessageSummary> summarizedMessages) {
        StringBuilder sb = new StringBuilder();
        sb.append("[이전 단계 요약]\n");
        for (ChatMessageSummary summary : summarizedMessages) {
            sb.append("- ").append(summary.getContent()).append("\n");
        }
        return sb.toString();
    }

    private Map<String, String> createMessageMap(SenderType senderType, String content) {
        return Map.of(
                "role", senderType.getApiName(),
                "content", content
        );
    }

    private String getMetaDataContent(Member member) {
        String memberMemoryList = memberMemoryDomainService.getMemberMemoriesByMemberId(MemberId.of(member.getId()));

        StringBuilder metadataBuilder = new StringBuilder();
        metadataBuilder.append("[사용자 메타데이터]\n");
        String nickname = member.getNickname();
        metadataBuilder.append("- 사용자 이름: ").append(nickname).append("\n");
        String dDayState = memberDomainService.getMemberDDayState(member.getStartLoveDate());
        metadataBuilder.append("- 연애 기간: ").append(dDayState).append("\n");

        LoadChatRoomMetadataPort.ChatRoomMetadataDto chatRoomMetadataDto = chatRoomDomainService.getChatRoomMetadata(MemberId.of(member.getId()));
        String memberLoveTypeTitle = chatRoomMetadataDto.memberLoveType() != null ? chatRoomMetadataDto.memberLoveType().getTitle() : "알 수 없음";
        metadataBuilder.append("- 사용자 애착 유형: ").append(memberLoveTypeTitle).append("\n");

        String partnerLoveType = chatRoomMetadataDto.partnerLoveType() != null ? chatRoomMetadataDto.partnerLoveType().getTitle() : "알 수 없음";
        metadataBuilder.append("- 애인 애착 유형: ").append(partnerLoveType).append("\n");
        metadataBuilder.append(memberMemoryList);

        return metadataBuilder.toString();
    }

}
