package makeus.cmc.malmo.adaptor.out.persistence.mapper;

import makeus.cmc.malmo.adaptor.out.persistence.entity.terms.TermsDetailsEntity;
import makeus.cmc.malmo.adaptor.out.persistence.entity.terms.TermsEntity;
import makeus.cmc.malmo.adaptor.out.persistence.entity.value.TermsEntityId;
import makeus.cmc.malmo.domain.model.terms.Terms;
import makeus.cmc.malmo.domain.model.terms.TermsDetails;
import makeus.cmc.malmo.domain.value.id.TermsId;
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

    public TermsDetails toDomain(TermsDetailsEntity entity) {
        return TermsDetails.from(
                entity.getId(),
                TermsId.of(entity.getTermsEntityId() != null ? entity.getTermsEntityId().getValue() : null),
                entity.getTermsDetailsType(),
                entity.getContent()
        );
    }

    public TermsDetailsEntity toEntity(TermsDetails domain) {
        return TermsDetailsEntity.builder()
                .id(domain.getId())
                .termsEntityId(domain.getTermsId() != null ? TermsEntityId.of(domain.getTermsId().getValue()) : null)
                .termsDetailsType(domain.getTermsDetailsType())
                .content(domain.getContent())
                .build();
    }

}