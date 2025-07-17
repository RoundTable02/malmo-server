package makeus.cmc.malmo.adaptor.out.persistence.repository.custom;

import makeus.cmc.malmo.adaptor.out.persistence.entity.value.InviteCodeEntityValue;
import makeus.cmc.malmo.application.port.out.LoadMemberPort;
import makeus.cmc.malmo.application.port.out.LoadPartnerPort;
import makeus.cmc.malmo.domain.model.love_type.LoveTypeCategory;

import java.util.Optional;

public interface MemberRepositoryCustom {
    Optional<LoadMemberPort.MemberResponseRepositoryDto> findMemberDetailsById(Long memberId);
    Optional<LoadPartnerPort.PartnerMemberRepositoryDto> findPartnerMember(Long memberId);

    boolean isCoupleMember(Long memberId);

    boolean existsByInviteCode(String inviteCode);

    boolean isAlreadyCoupleMemberByInviteCode(String inviteCode);

    Optional<InviteCodeEntityValue> findInviteCodeByMemberId(Long memberId);

    Optional<LoadPartnerPort.PartnerLoveTypeRepositoryDto> findPartnerLoveTypeCategory(Long memberId);

}
