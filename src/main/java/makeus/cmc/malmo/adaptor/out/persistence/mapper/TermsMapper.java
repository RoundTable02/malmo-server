package makeus.cmc.malmo.adaptor.out.persistence.mapper;

import makeus.cmc.malmo.adaptor.out.persistence.entity.terms.TermsEntity;
import makeus.cmc.malmo.domain.model.terms.Terms;
import org.springframework.stereotype.Component;

@Component
public class TermsMapper {

    public Terms toDomain(TermsEntity entity) {
        return Terms.from(
                entity.getId(),
                entity.getTitle(),
                entity.getContent(),
                entity.getVersion(),
                entity.isRequired(),
                entity.getTermsType(),
                entity.getCreatedAt(),
                entity.getModifiedAt(),
                entity.getDeletedAt()
        );
    }

    public TermsEntity toEntity(Terms domain) {
        return TermsEntity.builder()
                .id(domain.getId())
                .title(domain.getTitle())
                .content(domain.getContent())
                .version(domain.getVersion())
                .isRequired(domain.isRequired())
                .termsType(domain.getTermsType())
                .createdAt(domain.getCreatedAt())
                .modifiedAt(domain.getModifiedAt())
                .deletedAt(domain.getDeletedAt())
                .build();
    }
}