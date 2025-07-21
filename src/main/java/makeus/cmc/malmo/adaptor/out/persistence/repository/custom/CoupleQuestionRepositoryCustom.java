package makeus.cmc.malmo.adaptor.out.persistence.repository.custom;

import makeus.cmc.malmo.adaptor.out.persistence.CoupleQuestionPersistenceAdapter;
import makeus.cmc.malmo.adaptor.out.persistence.entity.question.CoupleQuestionEntity;

import java.util.Optional;

public interface CoupleQuestionRepositoryCustom {

    Optional<CoupleQuestionEntity> findTopLevelQuestionByCoupleId(Long coupleId);

    Optional<CoupleQuestionPersistenceAdapter.CoupleQuestionRepositoryDto> findTopLevelQuestionDto(Long memberId, Long coupleId);

    Optional<CoupleQuestionPersistenceAdapter.CoupleQuestionRepositoryDto> findQuestionDtoByLevel(Long memberId, Long coupleId, int level);
}
