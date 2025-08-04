package makeus.cmc.malmo.adaptor.out.persistence.repository.terms;

import makeus.cmc.malmo.adaptor.out.persistence.entity.terms.MemberTermsAgreementEntity;
import makeus.cmc.malmo.adaptor.out.persistence.entity.value.MemberEntityId;
import makeus.cmc.malmo.adaptor.out.persistence.entity.value.TermsEntityId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberTermsAgreementRepository extends JpaRepository<MemberTermsAgreementEntity, Long> {
    Optional<MemberTermsAgreementEntity> findByMemberEntityIdAndTermsEntityId(MemberEntityId memberEntityId, TermsEntityId termsEntityId);
}
