package makeus.cmc.malmo.application.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import makeus.cmc.malmo.application.port.in.SendChatMessageUseCase;
import makeus.cmc.malmo.domain.model.chat.ChatMessage;
import makeus.cmc.malmo.domain.model.chat.ChatRoom;
import makeus.cmc.malmo.domain.model.chat.SenderType;
import makeus.cmc.malmo.domain.model.member.Member;
import makeus.cmc.malmo.domain.model.value.ChatRoomId;
import makeus.cmc.malmo.domain.model.value.MemberId;
import makeus.cmc.malmo.domain.service.ChatMessagesDomainService;
import makeus.cmc.malmo.domain.service.ChatRoomDomainService;
import makeus.cmc.malmo.domain.service.MemberDomainService;
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

    @Override
    @Transactional
    public void processUserMessage(SendChatMessageUseCase.SendChatMessageCommand command) {
        Member member = memberDomainService.getMemberById(MemberId.of(command.getUserId()));

        // 과거 메시지 불러오기
        ChatRoom chatRoom = chatRoomDomainService.getCurrentChatRoomByMemberId(MemberId.of(member.getId()));
        List<ChatMessage> history = chatMessagesDomainService.getChatMessages(ChatRoomId.of(chatRoom.getId()));

        List<Map<String, String>> messages = new ArrayList<>();

        for (ChatMessage record : history) {
            messages.add(createMessageMap(record.getSenderType(), record.getContent()));
        }

        // 현재 메시지 추가
        messages.add(createMessageMap(SenderType.USER, command.getMessage()));
        chatMessagesDomainService.createUserTextMessage(ChatRoomId.of(chatRoom.getId()), command.getMessage());

        // OpenAI API 스트리밍 호출
        chatStreamProcessor.requestApiStream(
                MemberId.of(command.getUserId()),
                messages,
                ChatRoomId.of(chatRoom.getId()));
    }

    private Map<String, String> createMessageMap(SenderType senderType, String content) {
        return Map.of(
                "role", senderType.getApiName(),
                "content", content
        );
    }
}
