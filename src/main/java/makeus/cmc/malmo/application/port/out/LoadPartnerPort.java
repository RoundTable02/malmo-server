package makeus.cmc.malmo.application.port.out;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Optional;

public interface LoadPartnerPort {
    Optional<PartnerMemberRepositoryDto> loadPartnerByMemberId(Long memberId);

    @Data
    @AllArgsConstructor
    class PartnerMemberRepositoryDto {
        private String memberState;
        private Long loveTypeId;
        private String loveTypeTitle;
        private float avoidanceRate;
        private float anxietyRate;
        private String nickname;
    }
}
