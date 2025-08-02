package makeus.cmc.malmo.domain.service;

import lombok.RequiredArgsConstructor;
import makeus.cmc.malmo.application.port.out.*;
import makeus.cmc.malmo.domain.exception.ChatRoomNotFoundException;
import makeus.cmc.malmo.domain.exception.MemberAccessDeniedException;
import makeus.cmc.malmo.domain.exception.MemberNotFoundException;
import makeus.cmc.malmo.domain.exception.NotValidChatRoomException;
import makeus.cmc.malmo.domain.model.chat.ChatMessage;
import makeus.cmc.malmo.domain.model.chat.ChatRoom;
import makeus.cmc.malmo.domain.model.member.Member;
import makeus.cmc.malmo.domain.value.id.ChatRoomId;
import makeus.cmc.malmo.domain.value.id.MemberId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.List;

import static makeus.cmc.malmo.domain.model.chat.ChatRoomConstant.INIT_CHATROOM_LEVEL;
import static makeus.cmc.malmo.domain.model.chat.ChatRoomConstant.INIT_CHAT_MESSAGE;

@Component
@RequiredArgsConstructor
public class ChatRoomDomainService {

    public ChatRoom createChatRoom(MemberId memberId) {
        return ChatRoom.createChatRoom(memberId);
    }

    public ChatMessage createUserMessage(ChatRoomId chatRoomId, int level, String content) {
        return ChatMessage.createUserTextMessage(chatRoomId, level, content);
    }

    public ChatMessage createAiMessage(ChatRoomId chatRoomId, int level, String content) {
        return ChatMessage.createAssistantTextMessage(chatRoomId, level, content);
    }
}
