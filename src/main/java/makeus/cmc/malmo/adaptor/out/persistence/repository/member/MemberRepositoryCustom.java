package makeus.cmc.malmo.adaptor.out.persistence.repository.member;

import makeus.cmc.malmo.adaptor.out.persistence.adapter.MemberPersistenceAdapter;
import makeus.cmc.malmo.adaptor.out.persistence.entity.value.InviteCodeEntityValue;
import makeus.cmc.malmo.application.port.out.chat.LoadChatRoomMetadataPort;

import java.util.Optional;

public interface MemberRepositoryCustom {
    Optional<MemberPersistenceAdapter.MemberResponseRepositoryDto> findMemberDetailsById(Long memberId);
    Optional<MemberPersistenceAdapter.PartnerMemberRepositoryDto> findPartnerMember(Long memberId);

    boolean isCoupleMember(Long memberId);

    boolean existsByInviteCode(String inviteCode);

    boolean isPartnerCoupleMemberAlive(Long memberId);

    Optional<InviteCodeEntityValue> findInviteCodeByMemberId(Long memberId);

    Optional<LoadChatRoomMetadataPort.ChatRoomMetadataDto> loadChatRoomMetadata(Long memberId);

    boolean isMemberStateAlive(Long memberId);

    Optional<Long> findPartnerMemberId(Long memberId);
}
