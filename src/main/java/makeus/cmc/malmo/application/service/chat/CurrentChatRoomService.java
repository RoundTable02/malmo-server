package makeus.cmc.malmo.application.service.chat;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import makeus.cmc.malmo.adaptor.in.aop.CheckValidMember;
import makeus.cmc.malmo.application.port.in.CompleteChatRoomUseCase;
import makeus.cmc.malmo.application.port.in.GetCurrentChatRoomMessagesUseCase;
import makeus.cmc.malmo.application.port.in.GetCurrentChatRoomUseCase;
import makeus.cmc.malmo.application.port.out.LoadMessagesPort;
import makeus.cmc.malmo.application.service.helper.chat_room.ChatRoomCommandHelper;
import makeus.cmc.malmo.application.service.helper.chat_room.ChatRoomQueryHelper;
import makeus.cmc.malmo.application.service.helper.chat_room.PromptQueryHelper;
import makeus.cmc.malmo.application.service.helper.member.MemberQueryHelper;
import makeus.cmc.malmo.domain.model.chat.ChatMessage;
import makeus.cmc.malmo.domain.model.chat.ChatRoom;
import makeus.cmc.malmo.domain.model.chat.Prompt;
import makeus.cmc.malmo.domain.model.member.Member;
import makeus.cmc.malmo.domain.service.ChatRoomDomainService;
import makeus.cmc.malmo.domain.value.id.ChatRoomId;
import makeus.cmc.malmo.domain.value.id.MemberId;
import makeus.cmc.malmo.util.JosaUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import static makeus.cmc.malmo.domain.model.chat.ChatRoomConstant.INIT_CHATROOM_LEVEL;
import static makeus.cmc.malmo.domain.model.chat.ChatRoomConstant.INIT_CHAT_MESSAGE;

@Slf4j
@Service
@RequiredArgsConstructor
public class CurrentChatRoomService
        implements GetCurrentChatRoomUseCase, GetCurrentChatRoomMessagesUseCase, CompleteChatRoomUseCase {

    private final ChatRoomDomainService chatRoomDomainService;
    private final PromptQueryHelper promptQueryHelper;
    private final ChatPromptBuilder chatPromptBuilder;
    private final ChatProcessor chatProcessor;
    private final ChatRoomQueryHelper chatRoomQueryHelper;
    private final MemberQueryHelper memberQueryHelper;
    private final ChatRoomCommandHelper chatRoomCommandHelper;

    @Override
    @Transactional
    @CheckValidMember
    public GetCurrentChatRoomResponse getCurrentChatRoom(GetCurrentChatRoomCommand command) {
        // 현재 채팅방 가져오기
        ChatRoom currentChatRoom = chatRoomQueryHelper.getCurrentChatRoomByMemberId(MemberId.of(command.getUserId()))
                .map(chatRoom -> {
                    if (chatRoomDomainService.isChatRoomExpired(chatRoom.getLastMessageSentTime())) {
                        // 마지막 채팅 이후 하루가 지난 경우 채팅방 종료 처리
                        chatRoom.expire();
                        ChatRoom savedChatRoom = chatRoomCommandHelper.saveChatRoom(chatRoom);

                        // 채팅방 요약을 비동기로 요청
                        requestChatRoomSummaryAsync(savedChatRoom);

                        return createAndSaveNewChatRoom(MemberId.of(command.getUserId()));
                    }

                    return chatRoom;
                })
                .orElseGet(() -> {
                    // 현재 채팅방이 없으면 새로 생성
                    return createAndSaveNewChatRoom(MemberId.of(command.getUserId()));
                });

        return GetCurrentChatRoomResponse.builder()
                .chatRoomState(currentChatRoom.getChatRoomState())
                .build();
    }

    private ChatRoom createAndSaveNewChatRoom(MemberId memberId) {
        // 새로운 채팅방 생성
        Member member = memberQueryHelper.getMemberByIdOrThrow(memberId);
        ChatRoom chatRoom = chatRoomDomainService.createChatRoom(memberId);
        ChatRoom savedChatRoom = chatRoomCommandHelper.saveChatRoom(chatRoom);

        // 초기 메시지 생성 및 저장
        ChatMessage initMessage = chatRoomDomainService.createAiMessage(
                ChatRoomId.of(savedChatRoom.getId()),
                INIT_CHATROOM_LEVEL,
                JosaUtils.아야(member.getNickname())
                        + INIT_CHAT_MESSAGE);
        chatRoomCommandHelper.saveChatMessage(initMessage);
        return savedChatRoom;
    }

    private void requestChatRoomSummaryAsync(ChatRoom savedChatRoom) {
        CompletableFuture.runAsync(() -> {
            try {
                ChatProcessor.CounselingSummary summary = requestChatRoomSummary(savedChatRoom);

                savedChatRoom.updateChatRoomSummary(
                        summary.getTotalSummary(),
                        summary.getSituationKeyword(),
                        summary.getSolutionKeyword()
                );
                chatRoomCommandHelper.saveChatRoom(savedChatRoom);
                log.info("채팅방 요약 완료: chatRoomId={}", savedChatRoom.getId());
            } catch (Exception e) {
                log.error("채팅방 요약 처리 중 오류 발생: chatRoomId={}", savedChatRoom.getId(), e);
            }
        });
    }

    @Override
    @CheckValidMember
    public GetCurrentChatRoomMessagesResponse getCurrentChatRoomMessages(GetCurrentChatRoomMessagesCommand command) {
        // 현재 채팅방 가져오기
        ChatRoom currentChatRoom = chatRoomQueryHelper.getCurrentChatRoomByMemberIdOrThrow(MemberId.of(command.getUserId()));

        Page<LoadMessagesPort.ChatRoomMessageRepositoryDto> result =
                chatRoomQueryHelper.getChatMessagesDtoDesc(ChatRoomId.of(currentChatRoom.getId()), command.getPageable());

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
        ChatRoom chatRoom = chatRoomQueryHelper.getCurrentChatRoomByMemberIdOrThrow(MemberId.of(command.getUserId()));
        chatRoom.complete();

        // 완료된 채팅방의 요약을 요청
        ChatProcessor.CounselingSummary summary = requestChatRoomSummary(chatRoom);

        // 채팅방의 요약을 저장
        chatRoom.updateChatRoomSummary(
                summary.getTotalSummary(),
                summary.getSituationKeyword(),
                summary.getSolutionKeyword()
        );
        chatRoomCommandHelper.saveChatRoom(chatRoom);

        return CompleteChatRoomResponse.builder()
                .chatRoomId(chatRoom.getId())
                .build();
    }

    private ChatProcessor.CounselingSummary requestChatRoomSummary(ChatRoom chatRoom) {
        Prompt systemPrompt = promptQueryHelper.getSystemPrompt();
        Prompt totalSummaryPrompt = promptQueryHelper.getTotalSummaryPrompt();

        List<Map<String, String>> messages = chatPromptBuilder.createForTotalSummary(chatRoom);

        return chatProcessor.requestTotalSummary(messages, systemPrompt, totalSummaryPrompt);
    }
}

