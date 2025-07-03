package makeus.cmc.malmo.application.port.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import makeus.cmc.malmo.domain.model.member.MemberState;
import makeus.cmc.malmo.domain.model.member.Provider;

import java.time.LocalDate;
import java.util.Optional;

public interface LoadPartnerPort {
    Optional<PartnerMemberRepositoryDto> loadPartnerByMemberId(Long memberId);

    @Data
    @AllArgsConstructor
    class PartnerMemberRepositoryDto {
        private LocalDate loveStartDate;
        private String memberState;
        private String loveTypeTitle;
        private float avoidanceRate;
        private float anxietyRate;
        private String nickname;
    }
}
