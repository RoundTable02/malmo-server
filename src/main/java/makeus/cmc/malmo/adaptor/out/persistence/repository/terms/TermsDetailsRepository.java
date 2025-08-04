package makeus.cmc.malmo.adaptor.out.persistence.repository.terms;

import makeus.cmc.malmo.adaptor.out.persistence.entity.terms.TermsDetailsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TermsDetailsRepository extends JpaRepository<TermsDetailsEntity, Long> {

    @Query("select t from TermsDetailsEntity t where t.termsEntityId.value = ?1")
    List<TermsDetailsEntity> getTermsDetailsEntitiesByTermsEntityId(Long termsEntityId);
}
