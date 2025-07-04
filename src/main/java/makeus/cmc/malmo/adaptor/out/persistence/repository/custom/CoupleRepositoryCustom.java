package makeus.cmc.malmo.adaptor.out.persistence.repository.custom;

import makeus.cmc.malmo.application.port.out.LoadPartnerPort;

import java.util.Optional;

public interface CoupleRepositoryCustom {
    Optional<LoadPartnerPort.PartnerMemberRepositoryDto> findPartnerMember(Long memberId);
}
