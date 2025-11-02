package makeus.cmc.malmo.application.service.chat;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import makeus.cmc.malmo.adaptor.in.aop.CheckValidMember;
import makeus.cmc.malmo.adaptor.message.StreamChatMessage;
import makeus.cmc.malmo.adaptor.message.StreamMessageType;
import makeus.cmc.malmo.application.helper.chat_room.ChatRoomCommandHelper;
import makeus.cmc.malmo.application.helper.chat_room.ChatRoomQueryHelper;
import makeus.cmc.malmo.application.helper.chat_room.PromptQueryHelper;
import makeus.cmc.malmo.application.helper.member.MemberQueryHelper;
import makeus.cmc.malmo.application.helper.outbox.OutboxHelper;
import makeus.cmc.malmo.application.port.in.chat.SendChatMessageUseCase;
import makeus.cmc.malmo.domain.model.chat.ChatMessage;
import makeus.cmc.malmo.domain.model.chat.ChatRoom;
import makeus.cmc.malmo.domain.model.chat.Prompt;
import makeus.cmc.malmo.domain.model.member.Member;
import makeus.cmc.malmo.domain.service.ChatRoomDomainService;
import makeus.cmc.malmo.domain.value.id.ChatRoomId;
import makeus.cmc.malmo.domain.value.id.MemberId;
import makeus.cmc.malmo.domain.value.state.ChatRoomState;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.CompletableFuture;

//import static makeus.cmc.malmo.util.GlobalConstants.FINAL_MESSAGE;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatService implements SendChatMessageUseCase {

    private final ChatRoomDomainService chatRoomDomainService;
    private final ChatSseSender chatSseSender;

    private final MemberQueryHelper memberQueryHelper;
    private final ChatRoomQueryHelper chatRoomQueryHelper;
    private final ChatRoomCommandHelper chatRoomCommandHelper;
    private final PromptQueryHelper promptQueryHelper;

    private final OutboxHelper outboxHelper;

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
        Prompt prompt = promptQueryHelper.getGuidelinePrompt(chatRoom.getLevel());
        if (prompt.isForCompletedResponse()) {
            String finalMessage = prompt.getContent();
            return handleLastPrompt(chatRoom, command.getMessage(), finalMessage);
        }

        // 채팅방이 초기화되지 않은 상태인 경우 초기화
        if (chatRoom.getChatRoomState() == ChatRoomState.BEFORE_INIT) {
            chatRoom.updateChatRoomStateAlive();
        }

        // 현재 유저 메시지를 저장
        ChatMessage savedUserMessage = saveUserMessage(chatRoom, command.getMessage());

        // 채팅방의 마지막 메시지 전송 시간 갱신
        chatRoom.updateLastMessageSentTime();
        chatRoomCommandHelper.saveChatRoom(chatRoom);

        // 채팅 응답 API 요청 스트림에 추가
        outboxHelper.publish(
                StreamMessageType.REQUEST_CHAT_MESSAGE,
                new StreamChatMessage(
                        member.getId(),
                        chatRoom.getId(),
                        command.getMessage(),
                        chatRoom.getLevel(),
                        chatRoom.getDetailedLevel()
                )
        );

        return SendChatMessageResponse.builder()
                .messageId(savedUserMessage.getId())
                .build();
    }

    // upgradeChatRoom 메서드 제거 - 내부 로직으로 통합됨

    private SendChatMessageResponse handleLastPrompt(ChatRoom chatRoom, String userMessage, String finalMessage) {
        // 마지막 단계에서 고정된 메시지를 반복하여 전송
        ChatMessage savedUserMessage = saveUserMessage(chatRoom, userMessage);

        chatSseSender.sendLastResponse(chatRoom.getMemberId(), finalMessage);
        saveAiMessage(chatRoom.getMemberId(), ChatRoomId.of(chatRoom.getId()), 
                chatRoom.getLevel(), chatRoom.getDetailedLevel(), finalMessage);
        return SendChatMessageResponse.builder()
                .messageId(savedUserMessage.getId())
                .build();
    }

    private ChatMessage saveUserMessage(ChatRoom chatRoom, String message) {
        ChatMessage userMessage = chatRoomDomainService.createUserMessage(
                ChatRoomId.of(chatRoom.getId()), 
                chatRoom.getLevel(), 
                chatRoom.getDetailedLevel(),
                message);
        return chatRoomCommandHelper.saveChatMessage(userMessage);
    }

    private void saveAiMessage(MemberId memberId, ChatRoomId chatRoomId, int level, int detailedLevel, String fullAnswer) {
        ChatMessage aiTextMessage = chatRoomDomainService.createAiMessage(chatRoomId, level, detailedLevel, fullAnswer);
        ChatMessage savedMessage = chatRoomCommandHelper.saveChatMessage(aiTextMessage);
        chatSseSender.sendAiResponseId(memberId, savedMessage.getId());
    }
}
