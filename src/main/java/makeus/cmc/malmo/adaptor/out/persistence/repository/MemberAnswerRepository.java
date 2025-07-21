package makeus.cmc.malmo.adaptor.out.persistence.repository;

import makeus.cmc.malmo.adaptor.out.persistence.entity.question.MemberAnswerEntity;
import makeus.cmc.malmo.adaptor.out.persistence.repository.custom.MemberAnswerRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberAnswerRepository extends JpaRepository<MemberAnswerEntity, Long>, MemberAnswerRepositoryCustom {
}
