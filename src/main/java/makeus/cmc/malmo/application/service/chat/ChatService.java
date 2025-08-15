package makeus.cmc.malmo.application.service.chat;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import makeus.cmc.malmo.adaptor.in.aop.CheckValidMember;
import makeus.cmc.malmo.adaptor.message.RequestSummaryMessage;
import makeus.cmc.malmo.adaptor.message.StreamChatMessage;
import makeus.cmc.malmo.adaptor.message.StreamMessageType;
import makeus.cmc.malmo.application.helper.chat_room.ChatRoomCommandHelper;
import makeus.cmc.malmo.application.helper.chat_room.ChatRoomQueryHelper;
import makeus.cmc.malmo.application.helper.member.MemberQueryHelper;
import makeus.cmc.malmo.application.port.in.chat.SendChatMessageUseCase;
import makeus.cmc.malmo.application.port.out.chat.PublishStreamMessagePort;
import makeus.cmc.malmo.domain.model.chat.ChatMessage;
import makeus.cmc.malmo.domain.model.chat.ChatRoom;
import makeus.cmc.malmo.domain.model.member.Member;
import makeus.cmc.malmo.domain.service.ChatRoomDomainService;
import makeus.cmc.malmo.domain.value.id.ChatRoomId;
import makeus.cmc.malmo.domain.value.id.MemberId;
import makeus.cmc.malmo.domain.value.state.ChatRoomState;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static makeus.cmc.malmo.util.GlobalConstants.FINAL_MESSAGE;
import static makeus.cmc.malmo.util.GlobalConstants.LAST_PROMPT_LEVEL;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatService implements SendChatMessageUseCase {

    private final ChatRoomDomainService chatRoomDomainService;
    private final ChatSseSender chatSseSender;

    private final MemberQueryHelper memberQueryHelper;
    private final ChatRoomQueryHelper chatRoomQueryHelper;
    private final ChatRoomCommandHelper chatRoomCommandHelper;

    private final PublishStreamMessagePort publishStreamMessagePort;

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

        // 채팅 응답 API 요청 스트림에 추가
        publishStreamMessagePort.publish(
                StreamMessageType.REQUEST_CHAT_MESSAGE,
                new StreamChatMessage(
                        member.getId(),
                        chatRoom.getId(),
                        command.getMessage(),
                        chatRoom.getLevel()
                )
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
        Member member = memberQueryHelper.getMemberByIdOrThrow(memberId);
        ChatRoom chatRoom = chatRoomQueryHelper.getCurrentChatRoomByMemberIdOrThrow(memberId);
        int nowChatRoomLevel = chatRoom.getLevel();

        // 채팅방 업그레이드 처리
        chatRoom.upgradeChatRoom();
        chatRoomCommandHelper.saveChatRoom(chatRoom);

        // 다음 단계 프롬프트를 통한 AI 응답 요청 스트림에 추가
        publishStreamMessagePort.publish(
                StreamMessageType.REQUEST_CHAT_MESSAGE,
                new StreamChatMessage(
                        member.getId(),
                        chatRoom.getId(),
                        "",
                        nowChatRoomLevel + 1
                )
        );

        // 현재 단계 채팅에 대한 전체 요약 요청 스트림에 추가
        publishStreamMessagePort.publish(
                StreamMessageType.REQUEST_SUMMARY,
                new RequestSummaryMessage(chatRoom.getId(), nowChatRoomLevel)
        );

        // 다음 단계 상담 도달, 채팅방 활성화
        chatRoom.updateChatRoomStateAlive();
        chatRoomCommandHelper.saveChatRoom(chatRoom);
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
