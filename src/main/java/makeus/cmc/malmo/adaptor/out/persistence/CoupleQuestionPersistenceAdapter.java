package makeus.cmc.malmo.adaptor.out.persistence;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import makeus.cmc.malmo.adaptor.out.persistence.entity.question.CoupleQuestionEntity;
import makeus.cmc.malmo.adaptor.out.persistence.entity.question.TempCoupleQuestionEntity;
import makeus.cmc.malmo.adaptor.out.persistence.mapper.CoupleQuestionMapper;
import makeus.cmc.malmo.adaptor.out.persistence.mapper.QuestionMapper;
import makeus.cmc.malmo.adaptor.out.persistence.repository.CoupleQuestionRepository;
import makeus.cmc.malmo.adaptor.out.persistence.repository.QuestionRepository;
import makeus.cmc.malmo.adaptor.out.persistence.repository.TempCoupleQuestionRepository;
import makeus.cmc.malmo.application.port.out.*;
import makeus.cmc.malmo.domain.model.question.CoupleQuestion;
import makeus.cmc.malmo.domain.model.question.Question;
import makeus.cmc.malmo.domain.model.question.TempCoupleQuestion;
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
public class CoupleQuestionPersistenceAdapter
        implements LoadCoupleQuestionPort, LoadTempCoupleQuestionPort, LoadQuestionPort,
        SaveCoupleQuestionPort, SaveTempCoupleQuestionPort {

    private final CoupleQuestionRepository coupleQuestionRepository;
    private final TempCoupleQuestionRepository tempCoupleQuestionRepository;
    private final QuestionRepository questionRepository;

    private final CoupleQuestionMapper coupleQuestionMapper;
    private final QuestionMapper questionMapper;

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

    @Override
    public Optional<TempCoupleQuestion> loadTempCoupleQuestionByMemberId(MemberId memberId) {
        return tempCoupleQuestionRepository.findByMemberId_Value(memberId.getValue())
                .map(coupleQuestionMapper::toDomain);
    }

    @Override
    public CoupleQuestion saveCoupleQuestion(CoupleQuestion coupleQuestion) {
        CoupleQuestionEntity entity = coupleQuestionMapper.toEntity(coupleQuestion);
        CoupleQuestionEntity savedEntity = coupleQuestionRepository.save(entity);
        return coupleQuestionMapper.toDomain(savedEntity);
    }

    @Override
    public TempCoupleQuestion saveTempCoupleQuestion(TempCoupleQuestion tempCoupleQuestion) {
        TempCoupleQuestionEntity entity = coupleQuestionMapper.toEntity(tempCoupleQuestion);
        TempCoupleQuestionEntity savedEntity = tempCoupleQuestionRepository.save(entity);
        return coupleQuestionMapper.toDomain(savedEntity);
    }

    @Override
    public Optional<Question> loadQuestionByLevel(int level) {
        return questionRepository.findByLevel(level)
                .map(questionMapper::toDomain);
    }

    @Data
    @Builder
    @AllArgsConstructor
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
