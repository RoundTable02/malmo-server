package makeus.cmc.malmo.application.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import makeus.cmc.malmo.application.port.in.GetCurrentChatRoomUseCase;
import makeus.cmc.malmo.domain.model.chat.ChatRoom;
import makeus.cmc.malmo.domain.service.ChatRoomDomainService;
import makeus.cmc.malmo.domain.value.id.MemberId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ChatRoomService implements GetCurrentChatRoomUseCase {

    private final ChatRoomDomainService chatRoomDomainService;

    @Override
    @Transactional
    public GetCurrentChatRoomResponse getCurrentChatRoom(GetCurrentChatRoomCommand command) {
        // 현재 채팅방 가져오기
        ChatRoom currentChatRoom = chatRoomDomainService.getCurrentChatRoomByMemberId(MemberId.of(command.getUserId()));

        return GetCurrentChatRoomResponse.builder()
                .chatRoomStatus(currentChatRoom.getChatRoomState())
                .build();
    }

}
