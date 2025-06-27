package makeus.cmc.malmo.adaptor.out.persistence.mapper;

import makeus.cmc.malmo.adaptor.out.persistence.entity.terms.TermsEntity;
import makeus.cmc.malmo.domain.model.terms.Terms;
import org.springframework.stereotype.Component;

@Component
public class TermsMapper {

    public Terms toDomain(TermsEntity entity) {
        return Terms.builder()
                .id(entity.getId())
                .title(entity.getTitle())
                .content(entity.getContent())
                .version(entity.getVersion())
                .isRequired(entity.isRequired())
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
                .build();
    }
}