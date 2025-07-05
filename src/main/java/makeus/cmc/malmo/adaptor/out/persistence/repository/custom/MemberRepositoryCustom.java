package makeus.cmc.malmo.adaptor.out.persistence.repository.custom;

import makeus.cmc.malmo.application.port.out.LoadMemberPort;
import makeus.cmc.malmo.application.port.out.LoadPartnerPort;

import java.util.Optional;

public interface MemberRepositoryCustom {
    Optional<LoadMemberPort.MemberResponseRepositoryDto> findMemberDetailsById(Long memberId);
    Optional<LoadPartnerPort.PartnerMemberRepositoryDto> findPartnerMember(Long memberId);
}
