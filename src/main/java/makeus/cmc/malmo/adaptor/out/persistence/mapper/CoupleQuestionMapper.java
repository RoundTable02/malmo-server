package makeus.cmc.malmo.adaptor.out.persistence.mapper;

import makeus.cmc.malmo.adaptor.out.persistence.entity.question.CoupleQuestionEntity;
import makeus.cmc.malmo.adaptor.out.persistence.entity.question.CoupleQuestionStateJpa;
import makeus.cmc.malmo.domain.model.question.CoupleQuestion;
import makeus.cmc.malmo.domain.model.question.CoupleQuestionState;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class CoupleQuestionMapper {

    private final QuestionMapper questionMapper;
    private final CoupleMapper coupleMapper;

    public CoupleQuestionMapper(QuestionMapper questionMapper, CoupleMapper coupleMapper) {
        this.questionMapper = questionMapper;
        this.coupleMapper = coupleMapper;
    }

    public CoupleQuestion toDomain(CoupleQuestionEntity entity) {
        return CoupleQuestion.builder()
                .id(entity.getId())
                .question(questionMapper.toDomain(entity.getQuestion()))
                .couple(coupleMapper.toDomain(entity.getCouple()))
                .coupleQuestionState(toCoupleQuestionState(entity.getCoupleQuestionStateJpa()))
                .createdAt(entity.getCreatedAt())
                .modifiedAt(entity.getModifiedAt())
                .deletedAt(entity.getDeletedAt())
                .build();
    }

    public CoupleQuestionEntity toEntity(CoupleQuestion domain) {
        return CoupleQuestionEntity.builder()
                .id(domain.getId())
                .question(questionMapper.toEntity(domain.getQuestion()))
                .couple(coupleMapper.toEntity(domain.getCouple()))
                .coupleQuestionStateJpa(toCoupleQuestionStateJpa(domain.getCoupleQuestionState()))
                .build();
    }

    private CoupleQuestionState toCoupleQuestionState(CoupleQuestionStateJpa coupleQuestionStateJpa) {
        return Optional.ofNullable(coupleQuestionStateJpa)
                .map(cqs -> CoupleQuestionState.valueOf(cqs.name()))
                .orElse(null);
    }

    private CoupleQuestionStateJpa toCoupleQuestionStateJpa(CoupleQuestionState coupleQuestionState) {
        return Optional.ofNullable(coupleQuestionState)
                .map(cqs -> CoupleQuestionStateJpa.valueOf(cqs.name()))
                .orElse(null);
    }
}