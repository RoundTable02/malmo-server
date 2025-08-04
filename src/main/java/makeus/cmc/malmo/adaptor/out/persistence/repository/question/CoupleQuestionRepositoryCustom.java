package makeus.cmc.malmo.adaptor.out.persistence.repository.question;

import makeus.cmc.malmo.adaptor.out.persistence.adapter.CoupleQuestionPersistenceAdapter;
import makeus.cmc.malmo.adaptor.out.persistence.entity.question.CoupleQuestionEntity;

import java.util.Optional;

public interface CoupleQuestionRepositoryCustom {

    Optional<CoupleQuestionEntity> findTopLevelQuestionByCoupleId(Long coupleId);

    Optional<CoupleQuestionPersistenceAdapter.CoupleQuestionRepositoryDto> findTopLevelQuestionDto(Long memberId, Long coupleId);

    Optional<CoupleQuestionPersistenceAdapter.CoupleQuestionRepositoryDto> findQuestionDtoByLevel(Long memberId, Long coupleId, int level);

    int countCoupleQuestionsByMemberId(Long memberId);
}
