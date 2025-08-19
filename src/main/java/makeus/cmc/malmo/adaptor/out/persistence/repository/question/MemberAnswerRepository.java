package makeus.cmc.malmo.adaptor.out.persistence.repository.question;

import makeus.cmc.malmo.adaptor.out.persistence.entity.question.MemberAnswerEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface MemberAnswerRepository extends JpaRepository<MemberAnswerEntity, Long>, MemberAnswerRepositoryCustom {
}
