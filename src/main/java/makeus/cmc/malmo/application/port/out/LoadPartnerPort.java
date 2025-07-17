package makeus.cmc.malmo.application.port.out;

import lombok.AllArgsConstructor;
import lombok.Data;
import makeus.cmc.malmo.domain.model.love_type.LoveTypeCategory;
import makeus.cmc.malmo.domain.model.value.MemberId;

import java.util.Optional;

public interface LoadPartnerPort {
    Optional<PartnerMemberRepositoryDto> loadPartnerByMemberId(Long memberId);
    Optional<PartnerLoveTypeRepositoryDto> loadPartnerLoveTypeCategory(MemberId memberId);

    @Data
    @AllArgsConstructor
    class PartnerMemberRepositoryDto {
        private String memberState;
        private LoveTypeCategory loveTypeCategory;
        private float avoidanceRate;
        private float anxietyRate;
        private String nickname;
    }

    @Data
    @AllArgsConstructor
    class PartnerLoveTypeRepositoryDto {
        private LoveTypeCategory loveTypeCategory;
        private float avoidanceRate;
        private float anxietyRate;
    }
}
