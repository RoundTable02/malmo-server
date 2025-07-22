package makeus.cmc.malmo.adaptor.out.persistence.repository.custom;

import makeus.cmc.malmo.adaptor.out.persistence.MemberAnswerPersistenceAdapter;
import makeus.cmc.malmo.adaptor.out.persistence.entity.question.MemberAnswerEntity;

import java.util.Optional;

public interface MemberAnswerRepositoryCustom {
    Optional<MemberAnswerPersistenceAdapter.AnswerRepositoryDto> findAnswersDtoByCoupleQuestionId(Long memberId, Long coupleQuestionId);

    Optional<MemberAnswerEntity> findByCoupleQuestionIdAndCoupleMemberId(Long coupleQuestionEntityId, Long memberId);

    boolean existsByCoupleQuestionIdAndMemberId(Long coupleQuestionEntityId, Long memberId);

    Long countByCoupleQuestionIdAndMemberId(Long coupleQuestionEntityId);
}
