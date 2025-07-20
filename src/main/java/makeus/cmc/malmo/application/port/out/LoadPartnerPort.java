package makeus.cmc.malmo.application.port.out;

import lombok.AllArgsConstructor;
import lombok.Data;
import makeus.cmc.malmo.domain.value.type.LoveTypeCategory;

import java.util.Optional;

public interface LoadPartnerPort {
    Optional<PartnerMemberRepositoryDto> loadPartnerByMemberId(Long memberId);

    @Data
    @AllArgsConstructor
    class PartnerMemberRepositoryDto {
        private String memberState;
        private LoveTypeCategory loveTypeCategory;
        private float avoidanceRate;
        private float anxietyRate;
        private String nickname;
    }
}
