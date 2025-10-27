package makeus.cmc.malmo.adaptor.out.persistence.mapper;

import makeus.cmc.malmo.adaptor.out.persistence.entity.chat.MemberChatRoomMetadataEntity;
import makeus.cmc.malmo.domain.model.chat.MemberChatRoomMetadata;
import makeus.cmc.malmo.domain.value.id.ChatRoomId;
import makeus.cmc.malmo.domain.value.id.MemberId;
import org.springframework.stereotype.Component;

@Component
public class MemberChatRoomMetadataMapper {

    public MemberChatRoomMetadata toDomain(MemberChatRoomMetadataEntity entity) {
        return MemberChatRoomMetadata.from(
                entity.getId(),
                ChatRoomId.of(entity.getChatRoomId()),
                MemberId.of(entity.getMemberId()),
                entity.getLevel(),
                entity.getDetailedLevel(),
                entity.getTitle(),
                entity.getSummary(),
                entity.getCreatedAt()
        );
    }

    public MemberChatRoomMetadataEntity toEntity(MemberChatRoomMetadata domain) {
        return MemberChatRoomMetadataEntity.builder()
                .id(domain.getId())
                .chatRoomId(domain.getChatRoomId().getValue())
                .memberId(domain.getMemberId().getValue())
                .level(domain.getLevel())
                .detailedLevel(domain.getDetailedLevel())
                .title(domain.getTitle())
                .summary(domain.getSummary())
                .createdAt(domain.getCreatedAt())
                .build();
    }
}
