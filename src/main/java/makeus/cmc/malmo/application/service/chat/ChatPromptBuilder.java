package makeus.cmc.malmo.application.service.chat;

import lombok.RequiredArgsConstructor;
import makeus.cmc.malmo.application.helper.chat_room.ChatRoomQueryHelper;
import makeus.cmc.malmo.application.helper.chat_room.MemberChatRoomMetadataQueryHelper;
import makeus.cmc.malmo.application.port.out.chat.LoadChatRoomMetadataPort;
import makeus.cmc.malmo.domain.model.chat.ChatMessage;
import makeus.cmc.malmo.domain.model.chat.ChatMessageSummary;
import makeus.cmc.malmo.domain.model.chat.ChatRoom;
import makeus.cmc.malmo.domain.model.chat.MemberChatRoomMetadata;
import makeus.cmc.malmo.domain.model.member.Member;
import makeus.cmc.malmo.domain.model.member.MemberMemory;
import makeus.cmc.malmo.domain.service.MemberDomainService;
import makeus.cmc.malmo.domain.value.id.ChatRoomId;
import makeus.cmc.malmo.domain.value.id.MemberId;
import makeus.cmc.malmo.domain.value.type.SenderType;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ChatPromptBuilder {

    private final MemberDomainService memberDomainService;
    private final ChatRoomQueryHelper chatRoomQueryHelper;
    private final MemberChatRoomMetadataQueryHelper memberChatRoomMetadataQueryHelper;

    public List<Map<String, String>> createForProcessUserMessage(Member member, ChatRoom chatRoom, String userMessage) {
        List<Map<String, String>> messages = new ArrayList<>();
        int chatRoomLevel = chatRoom.getLevel();

        // 1. 사용자 메타데이터
        String metaDataContent = getMetaDataContent(member);
        messages.add(createMessageMap(SenderType.USER, metaDataContent));

        // 2. 이전 단계 요약본
        List<ChatMessageSummary> previousLevelsSummarizedMessages = chatRoomQueryHelper.getSummarizedMessages(ChatRoomId.of(chatRoom.getId()));
        if (!previousLevelsSummarizedMessages.isEmpty()) {
            String summarizedMessageContent = getSummarizedMessageContent(previousLevelsSummarizedMessages);
            messages.add(createMessageMap(SenderType.SYSTEM, summarizedMessageContent));
        }

        // 3. 현재 단계 메시지들
        List<ChatMessage> currentChatRoomMessages = chatRoomQueryHelper.getChatRoomLevelMessages(ChatRoomId.of(chatRoom.getId()), chatRoomLevel);
        for (ChatMessage chatMessage : currentChatRoomMessages) {
            messages.add(createMessageMap(chatMessage.getSenderType(), chatMessage.getContent()));
        }

        if (currentChatRoomMessages.isEmpty()) {
            // 만약 현재 단계 메시지가 없다면, 이전 단계 메시지를 가져온다. (현재 단계 최초 진입)
            currentChatRoomMessages = chatRoomQueryHelper.getChatRoomLevelMessages(ChatRoomId.of(chatRoom.getId()), chatRoomLevel - 1);
            for (ChatMessage chatMessage : currentChatRoomMessages) {
                messages.add(createMessageMap(chatMessage.getSenderType(), chatMessage.getContent()));
            }
        }

        // 4. 현재 사용자 메시지 추가
        messages.add(createMessageMap(SenderType.USER, userMessage));

        return messages;
    }

    public List<Map<String, String>> createForSummaryAsync(ChatRoom chatRoom) {
        List<Map<String, String>> messages = new ArrayList<>();
        int chatRoomLevel = chatRoom.getLevel();

        // 현재 단계 메시지들
        List<ChatMessage> currentChatRoomMessages = chatRoomQueryHelper.getChatRoomLevelMessages(ChatRoomId.of(chatRoom.getId()), chatRoomLevel);
        for (ChatMessage chatMessage : currentChatRoomMessages) {
            messages.add(createMessageMap(chatMessage.getSenderType(), chatMessage.getContent()));
        }
        return messages;
    }

    public List<Map<String, String>> createForTotalSummary(ChatRoom chatRoom) {
        List<Map<String, String>> messages = new ArrayList<>();
        List<ChatMessageSummary> summarizedMessages = chatRoomQueryHelper.getSummarizedMessages(ChatRoomId.of(chatRoom.getId()));

        if (summarizedMessages.isEmpty()) {
            List<ChatMessage> lastLevelMessages = chatRoomQueryHelper.getChatRoomLevelMessages(ChatRoomId.of(chatRoom.getId()), chatRoom.getLevel());
            for (ChatMessage lastLevelMessage : lastLevelMessages) {
                messages.add(
                        createMessageMap(lastLevelMessage.getSenderType(), lastLevelMessage.getContent())
                );
            }
        } else {
            StringBuilder sb = new StringBuilder();
            for (ChatMessageSummary summary : summarizedMessages) {
                sb.append("[").append(summary.getLevel()).append(" 단계 요약] \n");
                sb.append(summary.getContent()).append("\n");
            }
            messages.add(
                    createMessageMap(SenderType.SYSTEM, sb.toString())
            );
        }
        return messages;
    }

    private String getSummarizedMessageContent(List<ChatMessageSummary> summarizedMessages) {
        if (summarizedMessages == null || summarizedMessages.isEmpty()) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        sb.append("[이전 단계 요약] \n");
        for (ChatMessageSummary summary : summarizedMessages) {
            sb.append("- ").append(summary.getContent()).append("\n");
        }
        return sb.toString();
    }

    private Map<String, String> createMessageMap(SenderType senderType, String content) {
        return Map.of(
                "role", senderType.getApiName(),
                "content", content
        );
    }

    private String getMetaDataContent(Member member) {
        String memberMemoryList = getMemberMemoriesByMemberId(MemberId.of(member.getId()));

        StringBuilder metadataBuilder = new StringBuilder();
        metadataBuilder.append("[사용자 메타데이터] \n");
        String nickname = member.getNickname();
        metadataBuilder.append("- 사용자 이름: ").append(nickname).append("\n");
//        String dDayState = memberDomainService.getMemberDDayState(member.getStartLoveDate());
//        metadataBuilder.append("- 연애 기간: ").append(dDayState).append("\n");

        LoadChatRoomMetadataPort.ChatRoomMetadataDto chatRoomMetadataDto = chatRoomQueryHelper.getChatRoomMetadata(MemberId.of(member.getId()));
        String memberLoveTypeTitle = chatRoomMetadataDto.memberLoveType() != null ? chatRoomMetadataDto.memberLoveType().getTitle() : "알 수 없음";
        metadataBuilder.append("- 사용자 애착 유형: ").append(memberLoveTypeTitle).append("\n");

        String partnerLoveType = chatRoomMetadataDto.partnerLoveType() != null ? chatRoomMetadataDto.partnerLoveType().getTitle() : "알 수 없음";
        metadataBuilder.append("- 애인 애착 유형: ").append(partnerLoveType).append("\n");
        metadataBuilder.append(memberMemoryList);

        return metadataBuilder.toString();
    }

    public String getMemberMemoriesByMemberId(MemberId memberId) {
        StringBuilder sb = new StringBuilder();
        List<MemberMemory> memoryList = chatRoomQueryHelper.getMemberMemoriesByMemberId(memberId);

        for (MemberMemory memberMemory : memoryList) {
            sb.append("- ").append(memberMemory.getContent()).append("\n");
        }

        return sb.toString();
    }

    public List<Map<String, String>> createForSufficiencyCheck(Member member, ChatRoom chatRoom, int level, int detailedLevel) {
        List<Map<String, String>> messages = new ArrayList<>();

        // 1. 사용자 메타데이터
        String metaDataContent = getMetaDataContent(member);
        messages.add(createMessageMap(SenderType.USER, metaDataContent));

        // 2. 이전 단계 요약본
        List<ChatMessageSummary> previousLevelsSummarizedMessages = chatRoomQueryHelper.getSummarizedMessages(ChatRoomId.of(chatRoom.getId()));
        if (!previousLevelsSummarizedMessages.isEmpty()) {
            String summarizedMessageContent = getSummarizedMessageContent(previousLevelsSummarizedMessages);
            messages.add(createMessageMap(SenderType.SYSTEM, summarizedMessageContent));
        }

        // 3. MemberChatRoomMetadata 정보
        List<MemberChatRoomMetadata> metadataList = memberChatRoomMetadataQueryHelper.getMemberChatRoomMetadata(ChatRoomId.of(chatRoom.getId()));
        if (!metadataList.isEmpty()) {
            String metadataContent = getMemberChatRoomMetadataContent(metadataList);
            messages.add(createMessageMap(SenderType.SYSTEM, metadataContent));
        }

        // 4. 현재 단계 메시지들
        List<ChatMessage> currentChatRoomMessages = chatRoomQueryHelper.getChatRoomLevelAndDetailedLevelMessages(ChatRoomId.of(chatRoom.getId()), level, detailedLevel);
        for (ChatMessage chatMessage : currentChatRoomMessages) {
            messages.add(createMessageMap(chatMessage.getSenderType(), chatMessage.getContent()));
        }

        return messages;
    }

    public List<Map<String, String>> createForStageSummary(ChatRoom chatRoom, int level) {
        List<Map<String, String>> messages = new ArrayList<>();

        // 현재 단계 메시지들
        List<ChatMessage> currentChatRoomMessages = chatRoomQueryHelper.getChatRoomLevelMessages(ChatRoomId.of(chatRoom.getId()), level);
        for (ChatMessage chatMessage : currentChatRoomMessages) {
            messages.add(createMessageMap(chatMessage.getSenderType(), chatMessage.getContent()));
        }

        return messages;
    }

    public List<Map<String, String>> createForNextDetailedPrompt(Member member, ChatRoom chatRoom, int level, int nextDetailedLevel) {
        List<Map<String, String>> messages = new ArrayList<>();

        // 1. 사용자 메타데이터
        String metaDataContent = getMetaDataContent(member);
        messages.add(createMessageMap(SenderType.USER, metaDataContent));

        // 2. 이전 단계 요약본
        List<ChatMessageSummary> previousLevelsSummarizedMessages = chatRoomQueryHelper.getSummarizedMessages(ChatRoomId.of(chatRoom.getId()));
        if (!previousLevelsSummarizedMessages.isEmpty()) {
            String summarizedMessageContent = getSummarizedMessageContent(previousLevelsSummarizedMessages);
            messages.add(createMessageMap(SenderType.SYSTEM, summarizedMessageContent));
        }

        // 3. MemberChatRoomMetadata 정보
        List<MemberChatRoomMetadata> metadataList = memberChatRoomMetadataQueryHelper.getMemberChatRoomMetadata(ChatRoomId.of(chatRoom.getId()));
        if (!metadataList.isEmpty()) {
            String metadataContent = getMemberChatRoomMetadataContent(metadataList);
            messages.add(createMessageMap(SenderType.SYSTEM, metadataContent));
        }

        // 4. 현재 단계 메시지들 (이전 detailedLevel까지)
        List<ChatMessage> currentChatRoomMessages = chatRoomQueryHelper.getChatRoomLevelAndDetailedLevelMessages(ChatRoomId.of(chatRoom.getId()), level, nextDetailedLevel - 1);
        for (ChatMessage chatMessage : currentChatRoomMessages) {
            messages.add(createMessageMap(chatMessage.getSenderType(), chatMessage.getContent()));
        }

        return messages;
    }

    public List<Map<String, String>> createForNextStage(Member member, ChatRoom chatRoom, int nextLevel) {
        List<Map<String, String>> messages = new ArrayList<>();

        // 1. 사용자 메타데이터
        String metaDataContent = getMetaDataContent(member);
        messages.add(createMessageMap(SenderType.USER, metaDataContent));

        // 2. 이전 단계 요약본
        List<ChatMessageSummary> previousLevelsSummarizedMessages = chatRoomQueryHelper.getSummarizedMessages(ChatRoomId.of(chatRoom.getId()));
        if (!previousLevelsSummarizedMessages.isEmpty()) {
            String summarizedMessageContent = getSummarizedMessageContent(previousLevelsSummarizedMessages);
            messages.add(createMessageMap(SenderType.SYSTEM, summarizedMessageContent));
        }

        // 3. MemberChatRoomMetadata 정보
        List<MemberChatRoomMetadata> metadataList = memberChatRoomMetadataQueryHelper.getMemberChatRoomMetadata(ChatRoomId.of(chatRoom.getId()));
        if (!metadataList.isEmpty()) {
            String metadataContent = getMemberChatRoomMetadataContent(metadataList);
            messages.add(createMessageMap(SenderType.SYSTEM, metadataContent));
        }

        return messages;
    }

    private String getMemberChatRoomMetadataContent(List<MemberChatRoomMetadata> metadataList) {
        if (metadataList == null || metadataList.isEmpty()) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        sb.append("[사용자의 갈등 내용] \n");
        for (MemberChatRoomMetadata metadata : metadataList) {
            sb.append("- ").append(metadata.getTitle()).append(": ").append(metadata.getSummary()).append("\n");
        }
        return sb.toString();
    }
}
