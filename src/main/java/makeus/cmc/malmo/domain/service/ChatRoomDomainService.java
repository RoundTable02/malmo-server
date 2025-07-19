package makeus.cmc.malmo.domain.service;

import lombok.RequiredArgsConstructor;
import makeus.cmc.malmo.application.port.out.*;
import makeus.cmc.malmo.domain.exception.ChatRoomNotFoundException;
import makeus.cmc.malmo.domain.exception.MemberNotFoundException;
import makeus.cmc.malmo.domain.exception.NotValidChatRoomException;
import makeus.cmc.malmo.domain.model.chat.ChatMessage;
import makeus.cmc.malmo.domain.model.chat.ChatRoom;
import makeus.cmc.malmo.domain.model.chat.ChatRoomConstant;
import makeus.cmc.malmo.domain.model.member.Member;
import makeus.cmc.malmo.domain.value.id.ChatRoomId;
import makeus.cmc.malmo.domain.value.id.MemberId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static makeus.cmc.malmo.domain.model.chat.ChatRoomConstant.INIT_CHATROOM_LEVEL;
import static makeus.cmc.malmo.domain.model.chat.ChatRoomConstant.INIT_CHAT_MESSAGE;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ChatRoomDomainService {

    private final LoadChatRoomPort loadChatRoomPort;
    private final LoadMemberPort loadMemberPort;
    private final SaveChatRoomPort saveChatRoomPort;
    private final SaveChatMessagePort saveChatMessagePort;
    private final LoadChatRoomMetadataPort loadChatRoomMetadataPort;

    public ChatRoom getCurrentChatRoomByMemberId(MemberId memberId) {
        // 현재 채팅방이 존재하는지 확인하고, 없으면 초기 메시지와 함께 새로 생성
        return loadChatRoomPort.loadCurrentChatRoomByMemberId(memberId)
                .orElseGet(() -> {
                    Member member = loadMemberPort.loadMemberById(memberId.getValue())
                            .orElseThrow(MemberNotFoundException::new);
                    ChatRoom chatRoom = saveChatRoomPort.saveChatRoom(ChatRoom.createChatRoom(memberId));
                    ChatMessage initMessage = ChatMessage.createAssistantTextMessage(
                            ChatRoomId.of(chatRoom.getId()), INIT_CHATROOM_LEVEL, member.getNickname() + INIT_CHAT_MESSAGE);
                    saveChatMessagePort.saveChatMessage(initMessage);

                    return chatRoom;
                });
    }

    public void validateChatRoomAlive(MemberId memberId) {
        loadChatRoomPort.loadCurrentChatRoomByMemberId(memberId)
                .ifPresentOrElse(chatRoom -> {
                            if (!chatRoom.isChatRoomValid()) {
                                throw new NotValidChatRoomException();
                            }
                        }
                        , () -> {
                            throw new ChatRoomNotFoundException();
                        }
                );
    }


    public LoadChatRoomMetadataPort.ChatRoomMetadataDto getChatRoomMetadata(MemberId memberId) {
        return loadChatRoomMetadataPort.loadChatRoomMetadata(memberId)
                .map(
                        metadata -> new LoadChatRoomMetadataPort.ChatRoomMetadataDto(
                                metadata.memberLoveTypeTitle() != null ? metadata.memberLoveTypeTitle() : "알 수 없음",
                                metadata.partnerLoveTypeTitle() != null ? metadata.partnerLoveTypeTitle() : "알 수 없음"
                )).orElse(new LoadChatRoomMetadataPort.ChatRoomMetadataDto("알 수 없음", "알 수 없음"));
    }

    @Transactional
    public void saveChatRoom(ChatRoom chatRoom) {
        saveChatRoomPort.saveChatRoom(chatRoom);
    }

    @Transactional
    public void updateChatRoomStateToPaused(ChatRoomId chatRoomId) {
        ChatRoom chatRoom = loadChatRoomPort.loadChatRoomById(chatRoomId)
                .orElseThrow(ChatRoomNotFoundException::new);

        chatRoom.updateChatRoomStatePaused();
        saveChatRoom(chatRoom);
    }

    @Transactional
    public void updateChatRoomStateToNeedNextQuestion(ChatRoomId chatRoomId) {
        ChatRoom chatRoom = loadChatRoomPort.loadChatRoomById(chatRoomId)
                .orElseThrow(ChatRoomNotFoundException::new);

        chatRoom.upgradeChatRoom();
        saveChatRoom(chatRoom);
    }

    @Transactional
    public void updateChatRoomStateToAlive(ChatRoomId chatRoomId) {
        ChatRoom chatRoom = loadChatRoomPort.loadChatRoomById(chatRoomId)
                .orElseThrow(ChatRoomNotFoundException::new);

        chatRoom.updateChatRoomStateAlive();
        saveChatRoom(chatRoom);
    }

    @Transactional
    public void updateMemberPausedChatRoomStateToAlive(MemberId memberId) {
        loadChatRoomPort.loadPausedChatRoomByMemberId(memberId)
                        .ifPresent(
                                chatRoom -> {
                                    chatRoom.updateChatRoomStateNeedNextQuestion();
                                    saveChatRoomPort.saveChatRoom(chatRoom);
                                }
                        );
    }
}
