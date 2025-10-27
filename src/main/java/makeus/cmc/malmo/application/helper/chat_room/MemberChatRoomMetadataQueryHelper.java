package makeus.cmc.malmo.application.helper.chat_room;

import lombok.RequiredArgsConstructor;
import makeus.cmc.malmo.adaptor.out.persistence.repository.chat.MemberChatRoomMetadataRepository;
import makeus.cmc.malmo.adaptor.out.persistence.mapper.MemberChatRoomMetadataMapper;
import makeus.cmc.malmo.domain.model.chat.MemberChatRoomMetadata;
import makeus.cmc.malmo.domain.value.id.ChatRoomId;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class MemberChatRoomMetadataQueryHelper {

    private final MemberChatRoomMetadataRepository memberChatRoomMetadataRepository;
    private final MemberChatRoomMetadataMapper memberChatRoomMetadataMapper;

    public List<MemberChatRoomMetadata> getMemberChatRoomMetadata(ChatRoomId chatRoomId) {
        return memberChatRoomMetadataRepository.findAllByChatRoomId(chatRoomId.getValue())
                .stream()
                .map(memberChatRoomMetadataMapper::toDomain)
                .toList();
    }

    public List<MemberChatRoomMetadata> getMemberChatRoomMetadata(ChatRoomId chatRoomId, int level, int detailedLevel) {
        return memberChatRoomMetadataRepository.findByChatRoomIdAndLevelAndDetailedLevel(chatRoomId.getValue(), level, detailedLevel)
                .stream()
                .map(memberChatRoomMetadataMapper::toDomain)
                .toList();
    }
}
