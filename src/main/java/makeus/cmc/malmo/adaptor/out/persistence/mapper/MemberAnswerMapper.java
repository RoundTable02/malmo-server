package makeus.cmc.malmo.adaptor.out.persistence.mapper;

import makeus.cmc.malmo.adaptor.out.persistence.entity.question.MemberAnswerEntity;
import makeus.cmc.malmo.adaptor.out.persistence.entity.question.MemberAnswerStateJpa;
import makeus.cmc.malmo.domain.model.question.MemberAnswer;
import makeus.cmc.malmo.domain.model.question.MemberAnswerState;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class MemberAnswerMapper {

    private final CoupleQuestionMapper coupleQuestionMapper;
    private final CoupleMemberMapper coupleMemberMapper;

    public MemberAnswerMapper(CoupleQuestionMapper coupleQuestionMapper, CoupleMemberMapper coupleMemberMapper) {
        this.coupleQuestionMapper = coupleQuestionMapper;
        this.coupleMemberMapper = coupleMemberMapper;
    }

    public MemberAnswer toDomain(MemberAnswerEntity entity) {
        return MemberAnswer.builder()
                .id(entity.getId())
                .coupleQuestion(coupleQuestionMapper.toDomain(entity.getCoupleQuestion()))
                .coupleMember(coupleMemberMapper.toDomain(entity.getCoupleMember()))
                .answer(entity.getAnswer())
                .memberAnswerState(toMemberAnswerState(entity.getMemberAnswerStateJpa()))
                .createdAt(entity.getCreatedAt())
                .modifiedAt(entity.getModifiedAt())
                .deletedAt(entity.getDeletedAt())
                .build();
    }

    public MemberAnswerEntity toEntity(MemberAnswer domain) {
        return MemberAnswerEntity.builder()
                .id(domain.getId())
                .coupleQuestion(coupleQuestionMapper.toEntity(domain.getCoupleQuestion()))
                .coupleMember(coupleMemberMapper.toEntity(domain.getCoupleMember()))
                .answer(domain.getAnswer())
                .memberAnswerStateJpa(toMemberAnswerStateJpa(domain.getMemberAnswerState()))
                .build();
    }

    private MemberAnswerState toMemberAnswerState(MemberAnswerStateJpa memberAnswerStateJpa) {
        return Optional.ofNullable(memberAnswerStateJpa)
                .map(mas -> MemberAnswerState.valueOf(mas.name()))
                .orElse(null);
    }

    private MemberAnswerStateJpa toMemberAnswerStateJpa(MemberAnswerState memberAnswerState) {
        return Optional.ofNullable(memberAnswerState)
                .map(mas -> MemberAnswerStateJpa.valueOf(mas.name()))
                .orElse(null);
    }
}