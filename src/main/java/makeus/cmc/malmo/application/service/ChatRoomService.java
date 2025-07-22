package makeus.cmc.malmo.application.service;

import lombok.RequiredArgsConstructor;
import makeus.cmc.malmo.application.port.in.*;
import makeus.cmc.malmo.application.port.out.LoadMessagesPort;
import makeus.cmc.malmo.domain.model.chat.ChatMessageSummary;
import makeus.cmc.malmo.domain.model.chat.ChatRoom;
import makeus.cmc.malmo.domain.service.ChatMessagesDomainService;
import makeus.cmc.malmo.domain.service.ChatRoomDomainService;
import makeus.cmc.malmo.domain.value.id.ChatRoomId;
import makeus.cmc.malmo.domain.value.id.MemberId;
import org.springframework.data.domain.Page;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@RequiredArgsConstructor
@Service
public class ChatRoomService
        implements GetChatRoomSummaryUseCase, GetChatRoomListUseCase,
        GetChatRoomMessagesUseCase, DeleteChatRoomUseCase {

    private final ChatRoomDomainService chatRoomDomainService;
    private final ChatMessagesDomainService chatMessagesDomainService;

    @Override
    public GetChatRoomSummaryResponse getChatRoomSummary(GetChatRoomSummaryCommand command) {
        chatRoomDomainService.validateChatRoomOwnership(MemberId.of(command.getUserId()), ChatRoomId.of(command.getChatRoomId()));

        ChatRoom chatRoom = chatRoomDomainService.getChatRoomById(ChatRoomId.of(command.getChatRoomId()));
        if (!Objects.equals(chatRoom.getMemberId().getValue(), command.getUserId())) {
            throw new AccessDeniedException("User does not have access to this chat room");
        }

        String totalSummary = chatRoom.getTotalSummary();
        List<ChatMessageSummary> summarizedMessages = chatMessagesDomainService.getSummarizedMessages(ChatRoomId.of(chatRoom.getId()));
        String firstSummary = summarizedMessages.isEmpty() ? "" : summarizedMessages.get(0).getContent();
        String secondSummary = summarizedMessages.size() > 1 ? summarizedMessages.get(1).getContent() : "";
        String thirdSummary = summarizedMessages.size() > 2 ? summarizedMessages.get(2).getContent() : "";

        return GetChatRoomSummaryResponse.builder()
                .chatRoomId(chatRoom.getId())
                .totalSummary(totalSummary)
                .firstSummary(firstSummary)
                .secondSummary(secondSummary)
                .thirdSummary(thirdSummary)
                .build();
    }

    @Override
    public GetChatRoomListResponse getChatRoomList(GetChatRoomListCommand command) {
        Page<ChatRoom> chatRoomList = chatRoomDomainService.getCompletedChatRoomsByMemberId(
                MemberId.of(command.getUserId()), command.getKeyword(), command.getPageable()
        );

        List<GetChatRoomResponse> response = chatRoomList.getContent().stream()
                .map(chatRoom -> GetChatRoomResponse.builder()
                        .chatRoomId(chatRoom.getId())
                        .totalSummary(chatRoom.getTotalSummary())
                        .situationKeyword(chatRoom.getSituationKeyword())
                        .solutionKeyword(chatRoom.getSolutionKeyword())
                        .createdAt(chatRoom.getCreatedAt())
                        .build())
                .toList();

        return GetChatRoomListResponse.builder()
                .chatRoomList(response)
                .totalCount(chatRoomList.getTotalElements())
                .build();
    }

    @Override
    public GetCurrentChatRoomMessagesResponse getChatRoomMessages(GetChatRoomMessagesCommand command) {
        chatRoomDomainService.validateChatRoomOwnership(MemberId.of(command.getUserId()), ChatRoomId.of(command.getChatRoomId()));

        List<LoadMessagesPort.ChatRoomMessageRepositoryDto> chatMessagesDto = chatMessagesDomainService.getChatMessagesDtoAsc(
                ChatRoomId.of(command.getChatRoomId()), command.getPage(), command.getSize());

        List<GetChatRoomMessagesUseCase.ChatRoomMessageDto> list = chatMessagesDto
                .stream()
                .map(cm ->
                        GetChatRoomMessagesUseCase.ChatRoomMessageDto.builder()
                                .messageId(cm.getMessageId())
                                .senderType(cm.getSenderType())
                                .content(cm.getContent())
                                .createdAt(cm.getCreatedAt())
                                .isSaved(cm.isSaved())
                                .build())
                .toList();

        return GetCurrentChatRoomMessagesResponse.builder()
                .messages(list)
                .build();
    }

    @Override
    public void deleteChatRooms(DeleteChatRoomsCommand command) {
        // 모든 채팅방이 멤버 소유인지 검증
        chatRoomDomainService.validateChatRoomsOwnership(
                MemberId.of(command.getUserId()),
                command.getChatRoomIdList().stream().map(ChatRoomId::of).toList());

        chatRoomDomainService.deleteChatRooms(
                command.getChatRoomIdList().stream().map(ChatRoomId::of).toList()
        );
    }
}
