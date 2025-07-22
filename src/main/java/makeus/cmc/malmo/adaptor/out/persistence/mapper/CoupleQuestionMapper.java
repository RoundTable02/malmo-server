package makeus.cmc.malmo.adaptor.out.persistence.mapper;

import lombok.RequiredArgsConstructor;
import makeus.cmc.malmo.adaptor.out.persistence.entity.question.CoupleQuestionEntity;
import makeus.cmc.malmo.adaptor.out.persistence.entity.question.TempCoupleQuestionEntity;
import makeus.cmc.malmo.adaptor.out.persistence.entity.value.CoupleEntityId;
import makeus.cmc.malmo.adaptor.out.persistence.entity.value.MemberEntityId;
import makeus.cmc.malmo.domain.model.question.CoupleQuestion;
import makeus.cmc.malmo.domain.model.question.TempCoupleQuestion;
import makeus.cmc.malmo.domain.value.id.CoupleId;
import makeus.cmc.malmo.domain.value.id.MemberId;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class CoupleQuestionMapper {

    private final QuestionMapper questionMapper;

    public CoupleQuestion toDomain(CoupleQuestionEntity entity) {
        if (entity == null) {
            return null;
        }
        return CoupleQuestion.from(
                entity.getId(),
                questionMapper.toDomain(entity.getQuestion()),
                entity.getCoupleEntityId() == null ? null
                        : CoupleId.of(entity.getCoupleEntityId().getValue()),
                entity.getCoupleQuestionState(),
                entity.getBothAnsweredAt(),
                entity.getCreatedAt(),
                entity.getModifiedAt(),
                entity.getDeletedAt()
        );
    }

    public CoupleQuestionEntity toEntity(CoupleQuestion coupleQuestion) {
        if (coupleQuestion == null) {
            return null;
        }
        return CoupleQuestionEntity.builder()
                .id(coupleQuestion.getId())
                .question(questionMapper.toEntity(coupleQuestion.getQuestion()))
                .coupleEntityId(coupleQuestion.getCoupleId() == null ? null
                        : CoupleEntityId.of(coupleQuestion.getCoupleId().getValue()))
                .coupleQuestionState(coupleQuestion.getCoupleQuestionState())
                .bothAnsweredAt(coupleQuestion.getBothAnsweredAt())
                .createdAt(coupleQuestion.getCreatedAt())
                .modifiedAt(coupleQuestion.getModifiedAt())
                .deletedAt(coupleQuestion.getDeletedAt())
                .build();
    }

    public TempCoupleQuestion toDomain(TempCoupleQuestionEntity entity) {
        if (entity == null) {
            return null;
        }
        return TempCoupleQuestion.from(
                entity.getId(),
                questionMapper.toDomain(entity.getQuestion()),
                entity.getMemberId() == null ? null
                        : MemberId.of(entity.getMemberId().getValue()),
                entity.getAnswer(),
                entity.getCoupleQuestionState(),
                entity.getCreatedAt(),
                entity.getModifiedAt(),
                entity.getDeletedAt()
        );
    }

    public TempCoupleQuestionEntity toEntity(TempCoupleQuestion tempCoupleQuestion) {
        if (tempCoupleQuestion == null) {
            return null;
        }
        return TempCoupleQuestionEntity.builder()
                .id(tempCoupleQuestion.getId())
                .question(questionMapper.toEntity(tempCoupleQuestion.getQuestion()))
                .memberId(tempCoupleQuestion.getMemberId() == null ? null
                        : MemberEntityId.of(tempCoupleQuestion.getMemberId().getValue()))
                .answer(tempCoupleQuestion.getAnswer())
                .coupleQuestionState(tempCoupleQuestion.getCoupleQuestionState())
                .createdAt(tempCoupleQuestion.getCreatedAt())
                .modifiedAt(tempCoupleQuestion.getModifiedAt())
                .deletedAt(tempCoupleQuestion.getDeletedAt())
                .build();
    }
}
