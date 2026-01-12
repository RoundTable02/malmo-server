package makeus.cmc.malmo.application.service.chat;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import makeus.cmc.malmo.adaptor.in.aop.CheckValidMember;
import makeus.cmc.malmo.application.helper.chat_room.ChatRoomCommandHelper;
import makeus.cmc.malmo.application.helper.member.MemberQueryHelper;
import makeus.cmc.malmo.application.port.in.chat.CreateChatRoomUseCase;
import makeus.cmc.malmo.domain.model.chat.ChatMessage;
import makeus.cmc.malmo.domain.model.chat.ChatRoom;
import makeus.cmc.malmo.domain.model.member.Member;
import makeus.cmc.malmo.domain.service.ChatRoomDomainService;
import makeus.cmc.malmo.domain.value.id.ChatRoomId;
import makeus.cmc.malmo.domain.value.id.MemberId;
import makeus.cmc.malmo.util.JosaUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static makeus.cmc.malmo.util.GlobalConstants.INIT_CHATROOM_LEVEL;
import static makeus.cmc.malmo.util.GlobalConstants.INIT_CHAT_MESSAGE;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatRoomManagementService implements CreateChatRoomUseCase {

    private final ChatRoomDomainService chatRoomDomainService;
    private final MemberQueryHelper memberQueryHelper;
    private final ChatRoomCommandHelper chatRoomCommandHelper;

    @Override
    @Transactional
    @CheckValidMember
    public CreateChatRoomResponse createChatRoom(CreateChatRoomCommand command) {
        MemberId memberId = MemberId.of(command.getUserId());
        Member member = memberQueryHelper.getMemberByIdOrThrow(memberId);
        
        // 채팅방 생성 (즉시 ALIVE 상태)
        ChatRoom chatRoom = chatRoomDomainService.createChatRoom(memberId);
        ChatRoom savedChatRoom = chatRoomCommandHelper.saveChatRoom(chatRoom);

        // 초기 AI 메시지 생성 및 저장
        ChatMessage initMessage = chatRoomDomainService.createAiMessage(
                ChatRoomId.of(savedChatRoom.getId()),
                INIT_CHATROOM_LEVEL,
                1,
                JosaUtils.아야(member.getNickname()) + INIT_CHAT_MESSAGE);
        chatRoomCommandHelper.saveChatMessage(initMessage);
        
        log.info("새 채팅방 생성: chatRoomId={}, memberId={}", savedChatRoom.getId(), memberId.getValue());
        
        return CreateChatRoomResponse.builder()
                .chatRoomId(savedChatRoom.getId())
                .chatRoomState(savedChatRoom.getChatRoomState())
                .createdAt(savedChatRoom.getCreatedAt())
                .build();
    }
}
