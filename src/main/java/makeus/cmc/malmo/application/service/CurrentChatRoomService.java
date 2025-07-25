package makeus.cmc.malmo.application.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import makeus.cmc.malmo.adaptor.in.aop.CheckValidMember;
import makeus.cmc.malmo.application.port.in.CompleteChatRoomUseCase;
import makeus.cmc.malmo.application.port.in.GetCurrentChatRoomMessagesUseCase;
import makeus.cmc.malmo.application.port.in.GetCurrentChatRoomUseCase;
import makeus.cmc.malmo.application.port.out.LoadMessagesPort;
import makeus.cmc.malmo.domain.model.chat.ChatMessage;
import makeus.cmc.malmo.domain.model.chat.ChatMessageSummary;
import makeus.cmc.malmo.domain.model.chat.ChatRoom;
import makeus.cmc.malmo.domain.model.chat.Prompt;
import makeus.cmc.malmo.domain.service.ChatMessagesDomainService;
import makeus.cmc.malmo.domain.service.ChatRoomDomainService;
import makeus.cmc.malmo.domain.service.PromptDomainService;
import makeus.cmc.malmo.domain.value.id.ChatRoomId;
import makeus.cmc.malmo.domain.value.id.MemberId;
import makeus.cmc.malmo.domain.value.type.SenderType;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CurrentChatRoomService
        implements GetCurrentChatRoomUseCase, GetCurrentChatRoomMessagesUseCase, CompleteChatRoomUseCase {

    private final ChatRoomDomainService chatRoomDomainService;
    private final ChatMessagesDomainService chatMessagesDomainService;
    private final ChatStreamProcessor chatStreamProcessor;
    private final PromptDomainService promptDomainService;

    @Override
    @Transactional
    @CheckValidMember
    public GetCurrentChatRoomResponse getCurrentChatRoom(GetCurrentChatRoomCommand command) {
        // 현재 채팅방 가져오기
        ChatRoom currentChatRoom = chatRoomDomainService.getCurrentChatRoomByMemberId(MemberId.of(command.getUserId()));

        return GetCurrentChatRoomResponse.builder()
                .chatRoomState(currentChatRoom.getChatRoomState())
                .build();
    }

    @Override
    @CheckValidMember
    public GetCurrentChatRoomMessagesResponse getCurrentChatRoomMessages(GetCurrentChatRoomMessagesCommand command) {
        // 현재 채팅방 가져오기
        ChatRoom currentChatRoom = chatRoomDomainService.getCurrentChatRoomByMemberId(MemberId.of(command.getUserId()));

        Page<LoadMessagesPort.ChatRoomMessageRepositoryDto> result = chatMessagesDomainService.getChatMessagesDto(
                ChatRoomId.of(currentChatRoom.getId()), command.getPageable());
        List<ChatRoomMessageDto> list = result.stream().map(cm ->
                        ChatRoomMessageDto.builder()
                                .messageId(cm.getMessageId())
                                .senderType(cm.getSenderType())
                                .content(cm.getContent())
                                .createdAt(cm.getCreatedAt())
                                .isSaved(cm.isSaved())
                                .build())
                .toList();

        return GetCurrentChatRoomMessagesResponse.builder()
                .messages(list)
                .totalCount(result.getTotalElements())
                .build();
    }

    @Override
    @Transactional
    @CheckValidMember
    public CompleteChatRoomResponse completeChatRoom(CompleteChatRoomCommand command) {
        ChatRoom chatRoom = chatRoomDomainService.getCurrentChatRoomByMemberId(MemberId.of(command.getUserId()));
        chatRoomDomainService.completeChatRoom(chatRoom);

        Prompt systemPrompt = promptDomainService.getSystemPrompt();
        Prompt totalSummaryPrompt = promptDomainService.getTotalSummaryPrompt();
        List<ChatMessageSummary> summarizedMessages = chatMessagesDomainService.getSummarizedMessages(ChatRoomId.of(chatRoom.getId()));

        StringBuilder sb = new StringBuilder();
        List<Map<String, String>> messages = new ArrayList<>();

        if (summarizedMessages.isEmpty()) {
            List<ChatMessage> lastLevelMessages = chatMessagesDomainService.getChatRoomLevelMessages(ChatRoomId.of(chatRoom.getId()), chatRoom.getLevel());
            for (ChatMessage lastLevelMessage : lastLevelMessages) {
                messages.add(
                        Map.of(
                                "role", lastLevelMessage.getSenderType().getApiName(),
                                "content", lastLevelMessage.getContent()
                        )
                );
            }
        }
        else {
            for (ChatMessageSummary summary : summarizedMessages) {
                sb.append("[ " + summary.getLevel() + " 단계 요약]\n");
                sb.append(summary.getContent()).append("\n");
            }
            messages.add(
                    Map.of(
                            "role", SenderType.SYSTEM.getApiName(),
                            "content", sb.toString()
                    )
            );
        }

        chatStreamProcessor.requestTotalSummary(chatRoom, systemPrompt, totalSummaryPrompt, messages);

        return CompleteChatRoomResponse.builder()
                .chatRoomId(chatRoom.getId())
                .build();
    }
}
