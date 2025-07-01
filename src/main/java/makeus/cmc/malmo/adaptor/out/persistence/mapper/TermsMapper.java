package makeus.cmc.malmo.adaptor.out.persistence.mapper;

import makeus.cmc.malmo.adaptor.out.persistence.entity.member.ProviderJpa;
import makeus.cmc.malmo.adaptor.out.persistence.entity.terms.TermsEntity;
import makeus.cmc.malmo.adaptor.out.persistence.entity.terms.TermsTypeJpa;
import makeus.cmc.malmo.domain.model.member.Provider;
import makeus.cmc.malmo.domain.model.terms.Terms;
import makeus.cmc.malmo.domain.model.terms.TermsType;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class TermsMapper {

    public Terms toDomain(TermsEntity entity) {
        return Terms.builder()
                .id(entity.getId())
                .title(entity.getTitle())
                .content(entity.getContent())
                .version(entity.getVersion())
                .isRequired(entity.isRequired())
                .termsType(toTermsType(entity.getTermsType()))
                .createdAt(entity.getCreatedAt())
                .modifiedAt(entity.getModifiedAt())
                .deletedAt(entity.getDeletedAt())
                .build();
    }

    public TermsEntity toEntity(Terms domain) {
        return TermsEntity.builder()
                .id(domain.getId())
                .title(domain.getTitle())
                .content(domain.getContent())
                .version(domain.getVersion())
                .isRequired(domain.isRequired())
                .termsType(toProviderJpa(domain.getTermsType()))
                .build();
    }

    private TermsType toTermsType(TermsTypeJpa termsTypeJpa) {
        return Optional.ofNullable(termsTypeJpa)
                .map(t -> TermsType.valueOf(t.name()))
                .orElse(null);
    }

    private TermsTypeJpa toProviderJpa(TermsType termsType) {
        return Optional.ofNullable(termsType)
                .map(t -> TermsTypeJpa.valueOf(t.name()))
                .orElse(null);
    }
}