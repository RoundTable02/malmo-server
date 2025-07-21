package makeus.cmc.malmo.adaptor.out.persistence;

import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import makeus.cmc.malmo.adaptor.out.persistence.mapper.CoupleQuestionMapper;
import makeus.cmc.malmo.adaptor.out.persistence.repository.CoupleQuestionRepository;
import makeus.cmc.malmo.application.port.out.LoadCoupleQuestionPort;
import makeus.cmc.malmo.domain.model.question.CoupleQuestion;
import makeus.cmc.malmo.domain.service.CoupleQuestionDomainService;
import makeus.cmc.malmo.domain.value.id.CoupleId;
import makeus.cmc.malmo.domain.value.id.CoupleQuestionId;
import makeus.cmc.malmo.domain.value.id.MemberId;
import makeus.cmc.malmo.domain.value.state.CoupleQuestionState;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class CoupleQuestionPersistenceAdapter implements LoadCoupleQuestionPort {

    private final CoupleQuestionRepository coupleQuestionRepository;
    private final CoupleQuestionMapper coupleQuestionMapper;

    @Override
    public Optional<CoupleQuestion> loadMaxLevelCoupleQuestion(CoupleId coupleId) {
        return coupleQuestionRepository.findTopLevelQuestionByCoupleId(coupleId.getValue())
                .map(coupleQuestionMapper::toDomain);
    }

    @Override
    public Optional<CoupleQuestionDomainService.CoupleQuestionDto> getMaxLevelQuestionDto(MemberId memberId, CoupleId coupleId) {
        return coupleQuestionRepository.findTopLevelQuestionDto(memberId.getValue(), coupleId.getValue())
                .map(CoupleQuestionRepositoryDto::toDto);
    }

    @Override
    public Optional<CoupleQuestionDomainService.CoupleQuestionDto> getCoupleQuestionDtoByLevel(MemberId memberId, CoupleId coupleId, int level) {
        return coupleQuestionRepository.findQuestionDtoByLevel(memberId.getValue(), coupleId.getValue(), level)
                .map(CoupleQuestionRepositoryDto::toDto);
    }

    @Override
    public Optional<CoupleQuestion> loadCoupleQuestionById(CoupleQuestionId coupleQuestionId) {
        return coupleQuestionRepository.findById(coupleQuestionId.getValue())
                .map(coupleQuestionMapper::toDomain);
    }

    @Data
    @Builder
    public static class CoupleQuestionRepositoryDto {
        private Long id;
        private String title;
        private String content;
        private int level;
        private Long coupleId;
        private CoupleQuestionState coupleQuestionState;
        private LocalDateTime bothAnsweredAt;
        private boolean meAnswered;
        private boolean partnerAnswered;
        private LocalDateTime createdAt;

        public static CoupleQuestionRepositoryDto from(CoupleQuestionDomainService.CoupleQuestionDto dto) {
            return CoupleQuestionRepositoryDto.builder()
                    .id(dto.getId())
                    .title(dto.getTitle())
                    .content(dto.getContent())
                    .level(dto.getLevel())
                    .coupleId(dto.getCoupleId().getValue())
                    .coupleQuestionState(dto.getCoupleQuestionState())
                    .bothAnsweredAt(dto.getBothAnsweredAt())
                    .meAnswered(dto.isMeAnswered())
                    .partnerAnswered(dto.isPartnerAnswered())
                    .createdAt(dto.getCreatedAt())
                    .build();
        }

        public CoupleQuestionDomainService.CoupleQuestionDto toDto() {
            return CoupleQuestionDomainService.CoupleQuestionDto.builder()
                    .id(this.id)
                    .title(this.title)
                    .content(this.content)
                    .level(this.level)
                    .coupleId(CoupleId.of(this.coupleId))
                    .coupleQuestionState(this.coupleQuestionState)
                    .bothAnsweredAt(this.bothAnsweredAt)
                    .meAnswered(this.meAnswered)
                    .partnerAnswered(this.partnerAnswered)
                    .createdAt(this.createdAt)
                    .build();
        }
    }
}
