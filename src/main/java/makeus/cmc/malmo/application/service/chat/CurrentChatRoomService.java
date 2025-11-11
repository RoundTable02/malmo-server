package makeus.cmc.malmo.application.service.chat;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import makeus.cmc.malmo.adaptor.in.aop.CheckValidMember;
import makeus.cmc.malmo.adaptor.message.RequestTotalSummaryMessage;
import makeus.cmc.malmo.adaptor.message.StreamMessageType;
import makeus.cmc.malmo.application.helper.chat_room.ChatRoomCommandHelper;
import makeus.cmc.malmo.application.helper.chat_room.ChatRoomQueryHelper;
import makeus.cmc.malmo.application.helper.member.MemberQueryHelper;
import makeus.cmc.malmo.application.helper.outbox.OutboxHelper;
import makeus.cmc.malmo.application.port.in.chat.CompleteChatRoomUseCase;
import makeus.cmc.malmo.application.port.in.chat.GetCurrentChatRoomMessagesUseCase;
import makeus.cmc.malmo.application.port.in.chat.GetCurrentChatRoomUseCase;
import makeus.cmc.malmo.application.port.out.chat.LoadMessagesPort;
import makeus.cmc.malmo.domain.model.chat.ChatMessage;
import makeus.cmc.malmo.domain.model.chat.ChatRoom;
import makeus.cmc.malmo.domain.model.member.Member;
import makeus.cmc.malmo.domain.service.ChatRoomDomainService;
import makeus.cmc.malmo.domain.value.id.ChatRoomId;
import makeus.cmc.malmo.domain.value.id.MemberId;
import makeus.cmc.malmo.util.JosaUtils;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static makeus.cmc.malmo.util.GlobalConstants.INIT_CHATROOM_LEVEL;
import static makeus.cmc.malmo.util.GlobalConstants.INIT_CHAT_MESSAGE;

@Slf4j
@Service
@RequiredArgsConstructor
public class CurrentChatRoomService
        implements GetCurrentChatRoomUseCase, GetCurrentChatRoomMessagesUseCase, CompleteChatRoomUseCase {

    private final ChatRoomDomainService chatRoomDomainService;
    private final ChatRoomQueryHelper chatRoomQueryHelper;
    private final MemberQueryHelper memberQueryHelper;
    private final ChatRoomCommandHelper chatRoomCommandHelper;

    private final OutboxHelper outboxHelper;

    @Override
    @Transactional
    @CheckValidMember
    public GetCurrentChatRoomResponse getCurrentChatRoom(GetCurrentChatRoomCommand command) {
        // 현재 채팅방 가져오기
        ChatRoom currentChatRoom = chatRoomQueryHelper.getCurrentChatRoomByMemberId(MemberId.of(command.getUserId()))
                .map(chatRoom -> {
                    if (chatRoom.isStarted() && chatRoomDomainService.isChatRoomExpired(chatRoom.getLastMessageSentTime())) {
                        // 마지막 채팅 이후 하루가 지난 경우 채팅방 종료 처리
                        chatRoom.expire();
                        ChatRoom savedChatRoom = chatRoomCommandHelper.saveChatRoom(chatRoom);
                        outboxHelper.publish(
                                StreamMessageType.REQUEST_TOTAL_SUMMARY,
                                new RequestTotalSummaryMessage(savedChatRoom.getId())
                        );

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
                1,
                JosaUtils.아야(member.getNickname())
                        + INIT_CHAT_MESSAGE);
        chatRoomCommandHelper.saveChatMessage(initMessage);
        return savedChatRoom;
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
        chatRoom.completeByUser();
        chatRoomCommandHelper.saveChatRoom(chatRoom);

        // 완료된 채팅방의 요약을 요청
        log.info("채팅방 요약 요청 스트림 추가: chatRoomId={}", chatRoom.getId());
        outboxHelper.publish(
                StreamMessageType.REQUEST_TOTAL_SUMMARY,
                new RequestTotalSummaryMessage(chatRoom.getId())
        );

        // 사용자에게는 즉시 성공 응답 반환
        return CompleteChatRoomResponse.builder()
                .chatRoomId(chatRoom.getId())
                .build();
    }

}

