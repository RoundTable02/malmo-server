package makeus.cmc.malmo.adaptor.out.persistence.repository.question;

import makeus.cmc.malmo.adaptor.out.persistence.adapter.MemberAnswerPersistenceAdapter;
import makeus.cmc.malmo.adaptor.out.persistence.entity.question.MemberAnswerEntity;

import java.util.Optional;

public interface MemberAnswerRepositoryCustom {
    Optional<MemberAnswerPersistenceAdapter.AnswerRepositoryDto> findAnswersDtoByCoupleQuestionId(Long memberId, Long coupleQuestionId);

    Optional<MemberAnswerEntity> findByCoupleQuestionIdAndMemberId(Long coupleQuestionEntityId, Long memberId);

    boolean existsByCoupleQuestionIdAndMemberId(Long coupleQuestionEntityId, Long memberId);

    Long countByCoupleQuestionIdAndMemberId(Long coupleQuestionEntityId);
}
